package com.kelvin.shardingjdbc5x;

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
     * 垂直分片插入
     */
    @Test
    public void testInsertOrderAndUser() {
        User user = new User();
        user.setUname("kelvin哥");
        userMapper.insert(user);

        Order order = new Order();
        order.setOrderNo("1");
        order.setUserId(user.getId());
        order.setAmount(new BigDecimal(100));

        orderMapper.insert(order);
    }

}
