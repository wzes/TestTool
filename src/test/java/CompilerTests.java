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
        JavaCompilerUtils.CompilerJavaFile("/home/xuantang/IdeaProjects/TestUtil/src/main/java/com/tongji/test/TestClass.java",
                "/home/xuantang/IdeaProjects/TestUtil/target/classes/com/tongji/test");

    }
}
