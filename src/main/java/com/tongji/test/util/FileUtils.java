package com.tongji.test.util;

import com.tongji.test.model.TestData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class FileUtils {

    public static Object[][] readCsv(String filename, Class<?>[] paramCls) {

        List<TestData> testData = getTestData(filename, paramCls);
        int legalCount = getLegalCount(testData);
        Object[][] data = new Object[legalCount][paramCls.length];

        int row = 0;
        for (TestData td : testData) {
            if (td.isLegal()) {
                String[] params = td.getLine().split(",");
                for (int i = 0; i < paramCls.length; i++) {
                    try {
                        if (paramCls[i] == int.class) {
                            data[row][i] = Integer.parseInt(params[i]);
                        } else if (paramCls[i] == Integer.class) {
                            data[row][i] = Integer.valueOf(params[i]);
                        } else if (paramCls[i] == String.class) {
                            data[row][i] = String.valueOf(params[i]);
                        } else if (paramCls[i] == double.class) {
                            data[row][i] = Double.parseDouble(params[i]);
                        } else if (paramCls[i] == Double.class) {
                            data[row][i] = Double.valueOf(params[i]);
                        } else {
                            throw new IllegalArgumentException("exist error parameter type");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("parameter type wrong");
                    }
                }
                row++;
            }
        }
        return data;
    }

    public static List<TestData> getTestData(String filename, Class<?>[] paramCls) {

        List<TestData> testDatas = new ArrayList<TestData>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                TestData testData = new TestData();
                testData.setLine(line);
                if (!line.isEmpty()) {
                    String[] params = line.split(",");
                    for (int i = 0; i < paramCls.length; i++) {
                        if (params.length != paramCls.length) {
                            testData.setLegal(false);
                            break;
                        }
                        try {
                            if (paramCls[i] == int.class) {
                                Integer.parseInt(params[i]);
                            } else if (paramCls[i] == Integer.class) {
                                Integer.valueOf(params[i]);
                            } else if (paramCls[i] == String.class) {
                                String.valueOf(params[i]);
                            } else if (paramCls[i] == double.class) {
                                Double.parseDouble(params[i]);
                            } else if (paramCls[i] == Double.class) {
                                Double.valueOf(params[i]);
                            } else {
                                testData.setLegal(false);
                            }
                        } catch (NumberFormatException e) {
                            testData.setLegal(false);
                        }
                        testData.setLegal(true);
                    }
                } else {
                    testData.setLegal(false);
                }
                testDatas.add(testData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testDatas;
    }

    private static int getLegalCount(List<TestData> testData) {
        int count = 0;
        for (TestData testData1 : testData) {
            if (testData1.isLegal()) {
                count++;
            }
        }
        return count;
    }
}
