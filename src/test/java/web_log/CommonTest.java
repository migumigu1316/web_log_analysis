package web_log;

import com.phone.bean.common.Task;
import com.phone.dao.common.ITaskDao;
import com.phone.dao.common.impl.TaskDaoImpl;
import org.junit.Test;

/**
 * @Description: TODO
 * @ClassName: CommonTest
 * @Author: xqg
 * @Date: 2018/11/27 21:28
 */
public class CommonTest {
    @Test
    public void testLombok() {
        Task task = new Task();
        task.setTask_name("你好");
        System.out.println(task);
        System.out.println(task.getTask_name());
    }

    @Test
    public void testTaskDao() {
        ITaskDao dao = new TaskDaoImpl();
        System.out.println(dao.findTaskById(1));
    }
}
