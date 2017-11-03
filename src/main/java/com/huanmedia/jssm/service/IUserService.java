package com.huanmedia.jssm.service;

import com.huanmedia.jssm.pojo.User;

import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */

public interface IUserService {

    public int insert(User pojo);

    public int insertSelective(User pojo);

    public int insertList(List<User> pojos);

    public int update(User pojo);

    public User findbyId(int id);
    public List<User> findAll();
}
