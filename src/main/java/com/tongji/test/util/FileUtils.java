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

    /**
     * read data from csv file
     * @param filename
     * @param paramCls
     * @return
     */
    public static Object[][] readCsv(String filename, Class<?>[] paramCls) {

        List<TestData> testData = getTestDataFromFile(filename, paramCls);
        int legalCount = getLegalCount(testData);
        return getDataObjects(testData, legalCount, paramCls);
    }

    /**
     * read data from system input
     * @param input
     * @param paramCls
     * @return
     */
    public static Object[][] readInput(String input, Class<?>[] paramCls) {
        List<TestData> testData = getTestDataFromInput(input, paramCls);
        int legalCount = getLegalCount(testData);
        return getDataObjects(testData, legalCount, paramCls);

    }

    /**
     *
     * @param testData
     * @param legalCount
     * @param paramCls
     * @return
     */
    private static Object[][] getDataObjects(List<TestData> testData, int legalCount, Class<?>[] paramCls) {
        Object[][] data = new Object[legalCount][paramCls.length];
        int row = 0;
        for (TestData td : testData) {
            if (td.isLegal()) {
                String[] params = td.getLine().split(",");
                for (int i = 0; i < paramCls.length; i++) {
                    try {
                        if (paramCls[i] == int.class) {
                            data[row][i] = Integer.parseInt(params[i].trim());
                        } else if (paramCls[i] == Integer.class) {
                            data[row][i] = Integer.valueOf(params[i].trim());
                        } else if (paramCls[i] == String.class) {
                            data[row][i] = String.valueOf(params[i].trim());
                        } else if (paramCls[i] == double.class) {
                            data[row][i] = Double.parseDouble(params[i].trim());
                        } else if (paramCls[i] == Double.class) {
                            data[row][i] = Double.valueOf(params[i].trim());
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

    /**
     *
     * @param input
     * @param paramCls
     * @return
     */
    public static List<TestData> getTestDataFromInput(String input, Class<?>[] paramCls) {
        List<TestData> testDatas = new ArrayList<TestData>();
        String[] lines = input.split("\n");
        for (String line : lines) {
            TestData testData = getTestDataFromLine(paramCls, line);
            testDatas.add(testData);
        }
        return testDatas;
    }

    /**
     *
     * @param paramCls
     * @param line
     * @return
     */
    private static TestData getTestDataFromLine(Class<?>[] paramCls, String line) {
        TestData testData = new TestData();
        testData.setLine(line);
        if (!line.isEmpty()) {
            String[] params = line.split("," +
                    "");
            Object[] types = new Object[paramCls.length];
            testData.setData(params);
            testData.setTypes(types);
            for (int i = 0; i < paramCls.length; i++) {
                if (params.length != paramCls.length) {
                    testData.setLegal(false);
                    break;
                }
                try {
                    if (paramCls[i] == int.class) {
                        Integer.parseInt(params[i]);
                        types[i] = Integer.class;
                    } else if (paramCls[i] == Integer.class) {
                        Integer.valueOf(params[i]);
                        types[i] = Integer.class;
                    } else if (paramCls[i] == String.class) {
                        String.valueOf(params[i]);
                        types[i] = String.class;
                    } else if (paramCls[i] == double.class) {
                        Double.parseDouble(params[i]);
                        types[i] = double.class;
                    } else if (paramCls[i] == Double.class) {
                        Double.valueOf(params[i]);
                        types[i] = Double.class;
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
        return testData;
    }

    public static String getText(String filename, Class<?>[] paramCls) {
        List<TestData> testDataFromFile = getTestDataFromFile(filename, paramCls);
        StringBuilder sb = new StringBuilder();
        sb.append("input\t").append("legal").append("\n");
        for (TestData testData : testDataFromFile) {
            sb.append(testData.getLine()).append("\t")
                    .append(testData.isLegal())
            .append("\n");
        }
        return sb.toString();
    }

    /**
     *
     * @param filename
     * @param paramCls
     * @return
     */
    public static Object[] getTableTextTypes(String filename, Class<?>[] paramCls) {
        List<TestData> testDataFromFile = getTestDataFromFile(filename, paramCls);
        int column = 0;
        if (testDataFromFile != null && testDataFromFile.size() > 0) {
            column = testDataFromFile.get(0).getData().length;
        }
        Object[] types = new Object[column];
        assert testDataFromFile != null;
        for (int i = 0; i < column; i++) {
            types[i] = testDataFromFile.get(0).getTypes()[i];
        }
        return types;
    }


    public static String[][] getTableText(String filename, Class<?>[] paramCls) {
        List<TestData> testDataFromFile = getTestDataFromFile(filename, paramCls);
        int column = 0, row = 0;
        if (testDataFromFile != null && testDataFromFile.size() > 0) {
            column = testDataFromFile.get(0).getData().length;
            row = testDataFromFile.size();
        }
        String[][] data = new String[row][];
        assert testDataFromFile != null;
        for (int i = 0; i < testDataFromFile.size(); i++) {
            data[i] = new String[column + 1];
            for (int j = 0; j < column; j++) {
                data[i][j] = testDataFromFile.get(i).getData()[j];
            }
            data[i][column] = testDataFromFile.get(i).isLegal() + "";
        }
        return data;
    }

    /**
     *
     * @param filename
     * @param paramCls
     * @return
     */
    public static List<TestData> getTestDataFromFile(String filename, Class<?>[] paramCls) {

        List<TestData> testDatas = new ArrayList<TestData>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                TestData testData = getTestDataFromLine(paramCls, line);
                testDatas.add(testData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testDatas;
    }

    /**
     *
     * @param testData
     * @return
     */
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
