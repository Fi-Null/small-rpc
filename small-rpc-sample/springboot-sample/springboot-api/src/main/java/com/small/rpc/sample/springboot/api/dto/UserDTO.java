package com.small.rpc.sample.springboot.api.dto;

import java.io.Serializable;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 10:58 AM
 */
public class UserDTO implements Serializable {

    private String name;
    private String word;

    public UserDTO() {
    }

    public UserDTO(String name, String word) {
        this.name = name;
        this.word = word;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", word='" + word + '\'' +
                '}';
    }

}
