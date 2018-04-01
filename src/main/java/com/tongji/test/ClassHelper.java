package com.tongji.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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
            Method[] methods = cls.getMethods();
            mMethods.addAll(Arrays.asList(methods));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean execute(String methodName, Object[] params, Object expected) {
        Class<?>[] paramCls = getParamCls(methodName);
        try {
            Method method = cls.getDeclaredMethod(methodName, paramCls);
            Object obj = cls.newInstance();
            Object actual = method.invoke(obj, params);
            if (expected instanceof String) {
                return actual.equals(expected);
            } else {
                return actual.equals(expected);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Class<?>[] getParamCls(String methodName) {
        for (Method method : mMethods) {
            if (method.getName().equals(methodName)) {
                return method.getParameterTypes();
            }
        }
        return null;
    }
}
