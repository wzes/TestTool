package com.tongji.test.util;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Arrays;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class JavaCompilerUtils {
    private static JavaCompiler javaCompiler;

    private JavaCompilerUtils() {
    }

    private static JavaCompiler getJavaCompiler() {
        if (javaCompiler == null) {
            synchronized (JavaCompilerUtils.class) {
                if (javaCompiler == null) {
                    javaCompiler = ToolProvider.getSystemJavaCompiler();
                }
            }
        }

        return javaCompiler;
    }

    public static boolean CompilerJavaFile(String sourceFileInputPath,
                                           String classFileOutputPath) {

        Iterable<String> options = Arrays.asList("-d", classFileOutputPath);
        StandardJavaFileManager fileManager = getJavaCompiler()
                .getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(new File(
                        sourceFileInputPath)));

        return getJavaCompiler().getTask(null, fileManager, null, options,
                null, compilationUnits).call();
    }

    public static String getPackageName(String sourceFileInputPath){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFileInputPath)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("package")) {
                    return line.replace(" ", "").replace(".", "/")
                            .replace("\n", "")
                            .replace(";", "").replace("package", "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClassName (String sourceFileInputPath) {
        String packageName = getPackageName(sourceFileInputPath);
        int index = sourceFileInputPath.lastIndexOf("/");
        if (index != -1) {
            return packageName.replace("/", ".") + "." +
                    sourceFileInputPath.substring(index + 1).replace(".java", "");
        }
        return packageName.replace("/", ".") + "." + sourceFileInputPath.replace(".java", "");
    }
}
