package com.tongji.test.model;


/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class TestData {
    private String line;
    private boolean legal;
    private String[] data;

    public Object[] getTypes() {
        return types;
    }

    public void setTypes(Object[] types) {
        this.types = types;
    }

    private Object[] types;

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean isLegal() {
        return legal;
    }

    public void setLegal(boolean legal) {
        this.legal = legal;
    }
}
