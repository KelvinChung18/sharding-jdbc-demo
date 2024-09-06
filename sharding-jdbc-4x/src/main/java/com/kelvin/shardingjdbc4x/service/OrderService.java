package com.kelvin.shardingjdbc4x.service;

import com.kelvin.shardingjdbc4x.entity.OrderEntity;
import com.kelvin.shardingjdbc4x.repository.OrderRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class OrderService {

    @Resource
    private OrderRepository orderRepository;

    public void save(OrderEntity entity) {
        orderRepository.save(entity);
    }

}
