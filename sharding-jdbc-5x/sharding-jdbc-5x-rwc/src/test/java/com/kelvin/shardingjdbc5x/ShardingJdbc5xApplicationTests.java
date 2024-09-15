package com.kelvin.shardingjdbc5x;

import com.kelvin.shardingjdbc5x.entity.User;
import com.kelvin.shardingjdbc5x.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class ShardingJdbc5xApplicationTests {

    @Autowired
    private UserMapper userMapper;

    /**
     * 读写分离测试
     */
    @Test
    void testInsert() {
        // 写入数据的测试
        // 根据配置 写只会写在master中 读会从slave1/slave2中读取
        // 因为开启了MySQL主从，所以往Master写的时候 slave1/slave2会主动去发起同步
        User user = new User();
        user.setUname("win");
        userMapper.insert(user);
    }

    /**
     * 事务测试
     * 如果使用事务的话 读写都在master中（避免分布式事务）
     * 如果不使用事务的话 会触发读写分离（写在master 读在slave 具体根据配置算法）
     */
    @Test
    @Transactional // 加与不加有区分 加的
    public void testTrans() {

        User user = new User();
        user.setUname("man");
        userMapper.insert(user);

        List<User> users = userMapper.selectList(null);
    }

    @Test
    public void testLoadBalance() {

        List<User> users1 = userMapper.selectList(null);
        // 执行第二次测试负载均衡
        List<User> users2 = userMapper.selectList(null);

        users1.forEach(System.out::println);
        users2.forEach(System.out::println);
    }

}
