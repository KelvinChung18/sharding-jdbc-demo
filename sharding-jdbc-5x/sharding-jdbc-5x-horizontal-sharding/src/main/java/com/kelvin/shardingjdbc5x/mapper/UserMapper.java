package com.kelvin.shardingjdbc5x.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kelvin.shardingjdbc5x.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author kelvin
 * @date 2024/09/11 15:45
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}