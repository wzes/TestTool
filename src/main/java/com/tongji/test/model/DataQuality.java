package com.tongji.test.model;

import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class DataQuality {
    List<TestData> testData;
    private double legalRate;

    public DataQuality(List<TestData> testData) {
        this.testData = testData;
    }

    public DataQuality() {
    }

    public List<TestData> getTestData() {
        return testData;
    }

    public void setTestData(List<TestData> testData) {
        this.testData = testData;
    }

    public double getLegalRate() {
        int count = testData.size();
        int illegal = 0;
        for (TestData data : testData) {
            if (data.isLegal()) {
                illegal++;
            }
        }
        legalRate = (double) illegal / count;
        return legalRate;
    }

}
