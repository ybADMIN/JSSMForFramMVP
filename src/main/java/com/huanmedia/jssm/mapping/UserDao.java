package com.huanmedia.jssm.mapping;

import com.huanmedia.jssm.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface UserDao {
    int insert(@Param("pojo") User pojo);

    int insertSelective(@Param("pojo") User pojo);

    int insertList(@Param("pojos") List<User> pojo);

    int update(@Param("pojo") User pojo);

    User findbyId(@Param("id")Integer id);
    List<User> findAll();
}
