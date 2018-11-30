package web_log;

import com.phone.constant.Constants;
import com.phone.util.DBCPUtil;
import com.phone.util.ResourcesUtils;
import com.phone.util.StringUtils;
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

    @Test
    public void testStringUtil(){
        String v1 = "session_count=0|1s_3s=2|4s_6s...|60=0";
        String v2 = "session_count";
        String value = StringUtils.getFieldFromConcatString(v1,"\\|",v2);
        System.out.println(value);

        StringUtils.setFieldInConcatString(v1,"\\|",v2,String.valueOf(5));
        System.out.println(v1);
    }
}
