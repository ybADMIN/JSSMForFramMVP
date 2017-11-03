package com.huanmedia.jssm.service.impl;

import com.huanmedia.jssm.service.IUserService;
import com.huanmedia.jssm.mapping.UserDao;
import com.huanmedia.jssm.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service("userService")
public class UserService implements IUserService{

    @Resource
    private UserDao userDao;

    public int insert(User pojo){
        return userDao.insert(pojo);
    }

    public int insertSelective(User pojo){
        return userDao.insertSelective(pojo);
    }

    public int insertList(List<User> pojos){
        return userDao.insertList(pojos);
    }

    public int update(User pojo){
        return userDao.update(pojo);
    }

    public User findbyId(int id) {
        return userDao.findbyId(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }
}
