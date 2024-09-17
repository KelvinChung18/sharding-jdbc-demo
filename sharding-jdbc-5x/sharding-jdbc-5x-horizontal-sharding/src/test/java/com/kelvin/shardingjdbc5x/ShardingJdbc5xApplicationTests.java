package com.kelvin.shardingjdbc5x;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kelvin.shardingjdbc5x.entity.Order;
import com.kelvin.shardingjdbc5x.entity.User;
import com.kelvin.shardingjdbc5x.mapper.OrderMapper;
import com.kelvin.shardingjdbc5x.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class ShardingJdbc5xApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;


    /**
     * 插入order表（分库测试）
     * 对应配置文件：dev1
     * 规则：order表中`user_id`为偶数时，数据插入`server-order0服务器`，`user_id`为奇数时，数据插入`server-order1服务器`
     */
    @Test
    public void testInsertOrderDatabaseStrategy() {

        for (long i = 0; i < 4; i++) {
            Order order = new Order();
            order.setOrderNo("test");
            // 根据userId分库
            order.setUserId(i + 1);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }

    /**
     * 插入order表（分表测试）
     * 对应配置文件：dev2
     * 规则：根据orderNo的hash值进行取模运算
     */
    @Test
    public void testInsertOrderTableStrategy() {

        for (long i = 100; i < 104; i++) {
            Order order = new Order();

            order.setOrderNo("test" + i);
            // 根据userId分库
            order.setUserId(100L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }

    /**
     * 查询测试前置步骤...
     * 查询测试前的模拟数据（插入之前先把数据清空）
     * userId分库 userId为偶数的都在0库 奇数的在1库
     * order_no分表 order_no为偶数的都在0表 奇数的在1表
     */
    @Test
    public void testInsertSimulateData() {

        for (int i = 1; i < 4; i++) {

            Order order = new Order();
            order.setOrderNo("test" + i);
            // 根据userId分库
            order.setUserId(1L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }

        for (int i = 5; i < 9; i++) {

            Order order = new Order();
            order.setOrderNo("test" + i);
            // 根据userId分库
            order.setUserId(2L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }

    /**
     * 查询测试(前置条件中 在db_order0/1库的t_order0/1表都有记录 又分了库又分了表)
     * 水平分片查询所有记录
     */
    @Test
    public void testShardingSelectAll() {
        List<Order> orders = orderMapper.selectList(null);
        orders.forEach(System.out::println);
    }

    /**
     * 查询测试(前置条件中 在db_order0/1库的t_order0/1表都有记录 又分了库又分了表)
     * 水平分片查询指定条件的记录
     * userId分库 userId为偶数的都在0库 奇数的在1库
     * 这里shardingJDBC只会查询server-order1库 不会查询server-order0库 因为分片策略已经指定了(奇数在1库 userId=1为奇数)
     */
    @Test
    public void testShardingSelectByUserId() {

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1L);
        List<Order> orders = orderMapper.selectList(wrapper);
        orders.forEach(System.out::println);
    }


}
