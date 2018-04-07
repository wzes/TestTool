import org.junit.Assert;
import org.junit.Test;
import com.tongji.test.util.JavaCompilerUtils;

/**
 * @author Create by xuantang
 * @date on 4/1/18
 */
public class CompilerTests {

    @Test
    public void CompilerTest() {
        String className = JavaCompilerUtils.getClassName("src/main/java/com/tongji/test/TestClass.java");
        System.out.println(className);
        String packageName = JavaCompilerUtils.getPackageName("src/main/java/com/tongji/test/TestClass.java");
        System.out.println(packageName);
        JavaCompilerUtils.CompilerJavaFile("src/main/java/com/tongji/test/TestClass.java",
                "target/classes/com/tongji/test");

    }
}
