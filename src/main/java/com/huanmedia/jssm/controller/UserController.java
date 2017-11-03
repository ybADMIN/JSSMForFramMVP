package com.huanmedia.jssm.controller;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.huanmedia.jssm.pojo.User;
import com.huanmedia.jssm.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller  
@RequestMapping("/user")  
// /user/**
public class UserController {  
    private static Logger log=LoggerFactory.getLogger(UserController.class);
    @Resource
    private IUserService userService;

    // /user/test?id=1
    @RequestMapping(value="/test",method=RequestMethod.GET)  
    public String test(HttpServletRequest request,Model model){  
        int userId = Integer.parseInt(request.getParameter("id"));  
        System.out.println("userId:"+userId);
        User user=null;
        if (userId==1) {
             user = new User();  
             user.setAge(11);
             user.setId(1);
             user.setPassword("123");
             user.setUserName("javen");
        }
        log.debug(user.toString());
        model.addAttribute("user", user);  
        return "index";  
    }
    // /user/showUser?id=1
    @RequestMapping(value="/showUser",method=RequestMethod.GET)
    public String toIndex(HttpServletRequest request,Model model){
        int userId = Integer.parseInt(request.getParameter("id"));
        System.out.println("userId:"+userId);
        User user = this.userService.findbyId(userId);
        log.debug(user.toString());
        model.addAttribute("user", user);
        return "showUser";
    }
    @ResponseBody
    @RequestMapping(value = "/jsonTest",method = RequestMethod.POST)
    public Object getJasonUser(){
        JasonResponseBody<User> responseBody = new JasonResponseBody<User>();
        User itemsCustom = userService.findbyId(1);
        responseBody.setResult(itemsCustom);
        return responseBody;
    }
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public JasonResponseBody get(@PathVariable long id) {
        JasonResponseBody<User> responseBody = new JasonResponseBody<User>();
        User itemsCustom = userService.findbyId((int) id);
        responseBody.setResult(itemsCustom);
        return responseBody;
    }
    @ResponseBody
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public JasonResponseBody getUsers() {
        JasonResponseBody<List<User>> responseBody = new JasonResponseBody<List<User>>();
        List<User> itemsCustom = userService.findAll();
        List<User> users = new ArrayList<User>();

        for (int i = 0; i < 1 * 8; i++) {
            for (User user:
                 itemsCustom) {
                users.add(user);
            }
        }
        responseBody.setResult(users);
        return responseBody;
    }

   static class JasonResponseBody<T>{
        String code = "1";
        String message = "成功";
        T result;

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}