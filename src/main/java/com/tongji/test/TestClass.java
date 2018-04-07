package com.tongji.test;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class TestClass {
    private String name = "hello world";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int print(int i) {
        return i;
    }

    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
