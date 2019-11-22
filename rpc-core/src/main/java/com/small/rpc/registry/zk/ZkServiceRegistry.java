package com.small.rpc.registry.zk;

import com.small.rpc.registry.ServiceRegistry;
import com.small.rpc.util.RpcException;
import com.small.rpc.util.ZkClientUtil;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * service registry for "zookeeper"
 * <p>
 * /small-rpc/dev/
 * - key01(service01)
 * - value01 (ip:port01)
 * - value02 (ip:port02)
 *
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 8:11 PM
 */
public class ZkServiceRegistry implements ServiceRegistry {

    private static Logger logger = LoggerFactory.getLogger(ZkServiceRegistry.class);

    // param
    public static final String ENV = "env";               // zk env
    public static final String ZK_ADDRESS = "zkaddress";  // zk registry address, like "ip1:port,ip2:port,ip3:port"
    public static final String ZK_DIGEST = "zkdigest";    // zk registry digest

    // ------------------------------ zk conf ------------------------------

    // config
    private static final String zkBasePath = "/small-rpc";
    private String zkEnvPath;
    private ZkClientUtil zkClientUtil = null;

    private Thread refreshThread;
    private volatile boolean refreshThreadStop = false;

    private volatile ConcurrentMap<String, TreeSet<String>> registryData = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, TreeSet<String>> discoveryData = new ConcurrentHashMap<>();

    /**
     * key 2 path
     *
     * @param nodeKey
     * @return znodePath
     */
    public String keyToPath(String nodeKey) {
        return zkEnvPath + "/" + nodeKey;
    }

    /**
     * path 2 key
     *
     * @param nodePath
     * @return nodeKey
     */
    public String pathToKey(String nodePath) {
        if (nodePath == null || nodePath.length() <= zkEnvPath.length() || !nodePath.startsWith(zkEnvPath)) {
            return null;
        }
        return nodePath.substring(zkEnvPath.length() + 1, nodePath.length());
    }

    // ------------------------------ util ------------------------------

    /**
     * @param param Environment.ZK_ADDRESS  ：zk address
     *              Environment.ZK_DIGEST   ：zk didest
     *              Environment.ENV         ：env
     */
    @Override
    public void start(Map<String, String> param) {
        String zkaddress = param.get(ZK_ADDRESS);
        String zkdigest = param.get(ZK_DIGEST);
        String env = param.get(ENV);

        // valid
        if (zkaddress == null || zkaddress.trim().length() == 0) {
            throw new RpcException("small-rpc zkaddress can not be empty");
        }

        // init zkpath
        if (env == null || env.trim().length() == 0) {
            throw new RpcException("small-rpc env can not be empty");
        }

        zkEnvPath = zkBasePath.concat("/").concat(env);

        // init
        zkClientUtil = new ZkClientUtil(zkaddress, zkEnvPath, zkdigest, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    logger.debug(">>>>>>>>>>> small-rpc: watcher:{}", watchedEvent);

                    // session expire, close old and create new
                    if (watchedEvent.getState() == Event.KeeperState.Expired) {
                        zkClientUtil.destroy();
                        zkClientUtil.getClient();

                        // refreshDiscoveryData (all)：expire retry
                        refreshDiscoveryData(null);

                        logger.info(">>>>>>>>>>> small-rpc, zk re-connect reloadAll success.");
                    }

                    // watch + refresh
                    String path = watchedEvent.getPath();
                    String key = pathToKey(path);
                    if (key != null) {
                        // keep watch conf key：add One-time trigger
                        zkClientUtil.getClient().exists(path, true);

                        // refresh
                        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                            // refreshDiscoveryData (one)：one change
                            refreshDiscoveryData(key);
                        } else if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                            logger.info("reload all 111");
                        }
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        // init client      // TODO, support init without conn, and can use mirror data
        zkClientUtil.getClient();


        // refresh thread
        refreshThread = new Thread(() -> {
            while (!refreshThreadStop) {
                try {
                    TimeUnit.SECONDS.sleep(60);

                    // refreshDiscoveryData (all)：cycle check
                    refreshDiscoveryData(null);

                    // refresh RegistryData
                    refreshRegistryData();
                } catch (Exception e) {
                    if (!refreshThreadStop) {
                        logger.error(">>>>>>>>>>> small-rpc, refresh thread error.", e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> small-rpc, refresh thread stoped.");
        });
        refreshThread.setName("small-rpc, ZkServiceRegistry refresh thread.");
        refreshThread.setDaemon(true);
        refreshThread.start();

        logger.info(">>>>>>>>>>> small-rpc, ZkServiceRegistry init success. [env={}]", env);
    }

    @Override
    public void stop() {
        if (zkClientUtil != null) {
            zkClientUtil.destroy();
        }
        if (refreshThread != null) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }

    /**
     * refresh discovery data, and cache
     *
     * @param key
     */
    private void refreshDiscoveryData(String key) {

        Set<String> keys = new HashSet<>();
        if (key != null && key.trim().length() > 0) {
            keys.add(key);
        } else {
            if (discoveryData.size() > 0) {
                keys.addAll(discoveryData.keySet());
            }
        }

        if (keys.size() > 0) {
            for (String keyItem : keys) {

                // add-values
                String path = keyToPath(keyItem);
                Map<String, String> childPathData = zkClientUtil.getChildPathData(path);

                // exist-values
                TreeSet<String> existValues = discoveryData.get(keyItem);
                if (existValues == null) {
                    existValues = new TreeSet<String>();
                    discoveryData.put(keyItem, existValues);
                }

                if (childPathData.size() > 0) {
                    existValues.clear();
                    existValues.addAll(childPathData.keySet());
                }
            }
            logger.info(">>>>>>>>>>> small-rpc, refresh discovery data success, discoveryData = {}", discoveryData);
        }
    }

    /**
     * refresh registry data
     */
    private void refreshRegistryData() {
        if (registryData.size() > 0) {
            for (Map.Entry<String, TreeSet<String>> item : registryData.entrySet()) {
                String key = item.getKey();
                for (String value : item.getValue()) {
                    // make path, child path
                    String path = keyToPath(key);
                    zkClientUtil.setChildPathData(path, value, "");
                }
            }
            logger.info(">>>>>>>>>>> small-rpc, refresh registry data success, registryData = {}", registryData);
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        for (String key : keys) {
            // local cache
            TreeSet<String> values = registryData.get(key);
            if (values == null) {
                values = new TreeSet<>();
                registryData.put(key, values);
            }
            values.add(value);

            // make path, child path
            String path = keyToPath(key);
            zkClientUtil.setChildPathData(path, value, "");
        }
        logger.info(">>>>>>>>>>> small-rpc, registry success, keys = {}, value = {}", keys, value);
        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        for (String key : keys) {
            TreeSet<String> values = discoveryData.get(key);
            if (values != null) {
                values.remove(value);
            }
            String path = keyToPath(key);
            zkClientUtil.deleteChildPath(path, value);
        }
        logger.info(">>>>>>>>>>> small-rpc, remove success, keys = {}, value = {}", keys, value);
        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (keys == null || keys.size() == 0) {
            return null;
        }
        Map<String, TreeSet<String>> registryDataTmp = new HashMap<>();
        for (String key : keys) {
            TreeSet<String> valueSetTmp = discovery(key);
            if (valueSetTmp != null) {
                registryDataTmp.put(key, valueSetTmp);
            }
        }
        return registryDataTmp;
    }

    @Override
    public TreeSet<String> discovery(String key) {

        // local cache
        TreeSet<String> values = discoveryData.get(key);
        if (values == null) {

            // refreshDiscoveryData (one)：first use
            refreshDiscoveryData(key);

            values = discoveryData.get(key);
        }

        return values;
    }
}
