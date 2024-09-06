package com.kelvin.shardingjdbc4x.repository;

import com.kelvin.shardingjdbc4x.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
