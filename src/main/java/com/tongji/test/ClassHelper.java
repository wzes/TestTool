package com.tongji.test;

import com.tongji.test.model.DataQuality;
import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TotalResult;
import com.tongji.test.util.FileUtils;
import com.tongji.test.util.StatisticUtils;
import javafx.beans.binding.ObjectExpression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class ClassHelper {

    private ClassLoader classLoader;
    private Class<?> cls;
    private String className;
    private List<Method> mMethods;

    private String csvFilename;
    private String systemInput;

    private String[][] tableText;
    private Object[] types;

    public Object[] getTypes() {
        return types;
    }

    public void setTypes(Object[] types) {
        this.types = types;
    }

    public String getSystemInput() {
        return systemInput;
    }

    public void setSystemInput(String systemInput) {
        this.systemInput = systemInput;
    }

    public String getCsvFilename() {
        return csvFilename;
    }

    public void setCsvFilename(String csvFilename) {
        this.csvFilename = csvFilename;
    }

    public ClassHelper() {

    }

    public ClassHelper(String className) {
        this.className = className;
        mMethods = new ArrayList<Method>();
        classLoader = ClassHelper.class.getClassLoader();
        initMethods();
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void initMethods() {
        try {
            cls = classLoader.loadClass(className);
            Method[] methods = cls.getDeclaredMethods();
            mMethods.addAll(Arrays.asList(methods));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return
     */
    public List<String> getMethods() {
        List<String> methods = new ArrayList<String>();
        for (Method method : mMethods) {
            String methodName = method.getName();

            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();

            String returnName = returnType.toString();
            if (returnName.lastIndexOf('.') != -1) {
                returnName = returnName.substring(returnName.lastIndexOf('.') + 1);
            }
            if (parameterTypes != null && parameterTypes.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (Class cls : parameterTypes) {
                    String clsName = cls.toString();
                    if (clsName.lastIndexOf('.') != -1) {
                        clsName = clsName.substring(clsName.lastIndexOf('.') + 1);
                    }
                    sb.append(clsName).append(", ");
                }
                methods.add(methodName + "(" + sb.toString().substring(0, sb.toString().length() - 2) + ") :" + returnName);
            } else {
                methods.add(methodName + "() :" + returnName);
            }
        }
        return methods;
    }

    /**
     *
     * @param methodIndex
     * @return
     */
    public List<ItemResult> executeByCsv(int methodIndex) {
        return executeByCsv(mMethods.get(methodIndex));
    }

    /**
     *
     * @param methodIndex
     * @return
     */
    public List<ItemResult> executeByTableText(int methodIndex) {
        return executeByTableText(mMethods.get(methodIndex));
    }

    /**
     *
     * @param methodIndex
     * @return
     */
    public List<ItemResult> executeByInput(int methodIndex) {
        return executeByInput(mMethods.get(methodIndex));
    }

    /**
     *
     * @param methodName
     * @return
     */
    public List<ItemResult> executeByCsv(String methodName) {
        for (Method method : mMethods) {
            if (method.getName().equals(methodName)) {
                return executeByCsv(method);
            }
        }
        return null;
    }

    /**
     *
     * @param methodName
     * @return
     */
    public List<ItemResult> executeByInput(String methodName) {
        for (Method method : mMethods) {
            if (method.getName().equals(methodName)) {
                return executeByInput(method);
            }
        }
        return null;
    }

    public List<ItemResult> executeByInput(Method method) {
        // get input
        Object[][] input = FileUtils.readInput(systemInput, getParamAndReturnCls(method));
        return execute(method, input);
    }

    public List<ItemResult> executeByTableText(Method method) {
        Object[][] data = new Object[tableText.length][];
        // change the type for input data
        for (int i = 0; i < tableText.length; i++) {
            data[i] = new Object[tableText[i].length];
            for (int j = 0; j < tableText[0].length; j++) {
                try {
                    if (types[j] == int.class) {
                        data[i][j] = Integer.parseInt(tableText[i][j]);
                    } else if (types[j] == Integer.class) {
                        data[i][j] = Integer.valueOf(tableText[i][j]);
                    } else if (types[j] == String.class) {
                        data[i][j] = String.valueOf(tableText[i][j]);
                    } else if (types[j] == double.class) {
                        data[i][j] = Double.parseDouble(tableText[i][j]);
                    } else if (types[j] == Double.class) {
                        data[i][j] = Double.valueOf(tableText[i][j]);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return execute(method, data);
    }

    public List<ItemResult> executeByCsv(Method method) {
        // get input
        Object[][] input = FileUtils.readCsv(csvFilename, getParamAndReturnCls(method));
        return execute(method, input);
    }

    /**
     *
     * @param method
     * @return
     */
    private List<ItemResult> execute(Method method, Object[][] input) {
        List<ItemResult> itemResults = new ArrayList<ItemResult>();

        try {
            // get method
            Object obj = cls.newInstance();
            for (int row = 0; row < input.length; row++) {
                Object[] params = new Object[input[0].length - 1];
                int col;
                for (col = 0; col < input[0].length - 1; col++) {
                    params[col] = input[row][col];
                }
                Object actual = method.invoke(obj, params);

                // log
                ItemResult item = new ItemResult();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < col; i++) {
                    sb.append(String.valueOf(input[row][i])).append(" ");
                }

                item.setInput(sb.toString().substring(0, sb.toString().length() -1));
                item.setActual(actual);
                item.setExpected(input[row][col]);
                item.setCorrect(actual.equals(input[row][col]));
                itemResults.add(item);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return itemResults;
    }

    /**
     *
     * @param methodIndex
     * @return
     */
    public String getDataText(int methodIndex) {
        return FileUtils.getText(csvFilename, getParamAndReturnCls(mMethods.get(methodIndex)));
    }

    /**
     *
     * @param methodIndex
     * @return
     */
    public String[][] getTableText(int methodIndex) {
        this.setTypes(FileUtils.getTableTextTypes(csvFilename, getParamAndReturnCls(mMethods.get(methodIndex))));
        return FileUtils.getTableText(csvFilename, getParamAndReturnCls(mMethods.get(methodIndex)));
    }

    public DataQuality dataEvaluate(int methodIndex) {
        return new DataQuality(FileUtils.getTestDataFromFile(csvFilename,
                getParamAndReturnCls(mMethods.get(methodIndex))));
    }

    public void updateTable() {
        for (int i = 0; i < tableText.length; i++) {
            for (int j = 0; j < types.length; j++) {
                if (tableText[i].length - 1 != types.length) {
                    tableText[i][tableText[i].length - 1] = "false";
                    break;
                }
                try {
                    if (types[j] == int.class) {
                        Integer.parseInt(tableText[i][j]);
                    } else if (types[j] == Integer.class) {
                        Integer.valueOf(tableText[i][j]);
                    } else if (types[j] == String.class) {
                        String.valueOf(tableText[i][j]);
                    } else if (types[j] == double.class) {
                        Double.parseDouble(tableText[i][j]);
                    } else if (types[j] == Double.class) {
                        Double.valueOf(tableText[i][j]);
                    } else {
                        tableText[i][tableText[i].length - 1] = "false";
                        break;
                    }
                } catch (NumberFormatException e) {
                    tableText[i][tableText[i].length - 1] = "false";
                    break;
                }
                tableText[i][tableText[i].length - 1] = "true";
            }
        }
    }

    /**
     *
     * @param itemResults
     * @return
     */
    public TotalResult evaluate(List<ItemResult> itemResults) {
        return StatisticUtils.evaluate(itemResults);
    }


    public Class<?>[] getParamAndReturnCls(String methodName) {
        for (Method method : mMethods) {
            if (method.getName().equals(methodName)) {
                return getParams(method);
            }
        }
        return null;
    }

    private Class<?>[] getParams(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?>[] res = new Class[parameterTypes.length + 1];
        int index = 0;
        while (index < parameterTypes.length) {
            res[index] = parameterTypes[index];
            index++;
        }
        res[index] = method.getReturnType();
        return res;
    }

    public Class<?>[] getParamAndReturnCls(Method method) {
        return getParams(method);
    }

    public String[][] getTableText() {
        return tableText;
    }

    public void setTableText(String[][] tableText) {
        this.tableText = tableText;
    }
}
