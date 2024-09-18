package com.example.informationform;

import java.io.Serializable;
import java.util.UUID;

public class Person implements Serializable {
    private String name;
    private int age;
    private String id;

    public Person (String name, int age) {
        this.name = name;
        this.age = age;
        this.id = UUID.randomUUID().toString(); // 為每個 Person 生成唯一識別碼
    }

    public Person(String name, int age, String id) {
        this.name = name;
        this.age = age;
        this.id = id; // 使用從 SharedPreferences 讀取的 ID
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

}
