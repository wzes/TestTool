import org.junit.Assert;
import org.junit.Test;
import com.tongji.test.ClassHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class ExecuteTests {


    @Test
    public void ExecuteTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassHelper classHelper = new ClassHelper("com.tongji.test.TestClass");
        Assert.assertEquals(true,
                classHelper.execute("print", new Object[]{1}, 1));
    }
}
