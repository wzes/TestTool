import com.alibaba.fastjson.JSON;
import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TestData;
import com.tongji.test.model.TotalResult;
import com.tongji.test.util.FileUtils;
import com.tongji.test.util.StatisticUtils;
import org.junit.Test;
import com.tongji.test.ClassHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class ExecuteTests {


    @Test
    public void ExecuteTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassHelper classHelper = new ClassHelper("com.tongji.test.TestClass");

        Class<?>[] paramAndReturnCls = classHelper.getParamAndReturnCls("add");
        String filename = "/home/xuantang/IdeaProjects/TestUtil/data/test.csv";

        classHelper.setCsvFilename(filename);
        Object[][] objects;
        try {

            List<TestData> testData = FileUtils.getTestDataFromFile(filename, paramAndReturnCls);

            double quality = StatisticUtils.evaluateData(testData);

            System.out.println("The quality of data is " + quality * 100 + "%");
            System.out.println("--------------------------------");

            objects = FileUtils.readCsv(filename, paramAndReturnCls);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() +
                    "\nFile Error ! Please check the file: " + filename);
            return;
        }

        for (int i = 0; i < objects.length; i++) {
            for (int j =0; j < objects[0].length; j++) {
                System.out.print(objects[i][j] + " ");
            }
            System.out.print("\n");
        }

        System.out.println("--------------------------------");


        /**
         * item list result
         */

        List<ItemResult> resultList = classHelper.executeByCsv("add");

        System.out.println(JSON.toJSONString(resultList));

        System.out.println("--------------------------------");

        /**
         * result
         */
        TotalResult evaluate = classHelper.evaluate(resultList);

        System.out.println(JSON.toJSONString(evaluate));

        System.out.println("--------------------------------");

        /**
         * get the method list
         */
        List<String> methods = classHelper.getMethods();
        for (String method : methods) {
            System.out.println(method);
        }
    }
}
