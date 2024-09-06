package com.kelvin.shardingjdbc4x.controller;

import com.kelvin.shardingjdbc4x.entity.OrderEntity;
import com.kelvin.shardingjdbc4x.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/save")
    public String save(@RequestParam("userId") Integer userId) {
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userId);
        System.out.println("插入数据::::" + userId);
        orderService.save(entity);
        return "ok";
    }
}
