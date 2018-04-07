package com.tongji.test.util;

import java.io.*;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class FileUtils {

    public static Object[][] readCsv(String filename, Class<?>[] paramCls) {
        int[] shape;
        shape = checkFile(filename, paramCls.length);


        Object[][] data = new Object[shape[0]][shape[1]];
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

            String line;
            int row = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] params = line.split(",");
                    for (int i = 0; i < params.length; i++) {
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
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("file not found");
        } catch (IOException e) {
            throw new IllegalArgumentException("io exception");
        }
        return data;
    }

    private static int[] checkFile(String filename, int col) {
        int[] shape = new int[2];
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));


            String line;
            int row = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] params = line.split(",");
                    if (params.length != col) {
                        throw new IllegalArgumentException("Column number is illegal");
                    }
                    row++;
                }
            }
            shape[0] = row;
            shape[1] = col;
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shape;
    }

}
