package web_log;

import com.phone.constant.Constants;
import com.phone.util.DBCPUtil;
import com.phone.util.ResourcesUtils;
import org.junit.Test;

import java.sql.Connection;


/**
 * @Description: TODO 工具类测试
 * @ClassName: UtilTest
 * @Author: xqg
 * @Date: 2018/11/26 20:32
 */
public class UtilTest {
    @Test
    public void testResourceUtil(){
        String mode =
                ResourcesUtils.getPropertyValueByKey(Constants.SPARK_JOB_DEPLOY_MODE);
        System.out.println("部署模式：" + mode);
    }

    @Test
    public void testDBCPUtil(){//测试时保证数据库打开状态
        Connection conn = DBCPUtil.getConnection();
        System.out.println(conn);
    }
}
