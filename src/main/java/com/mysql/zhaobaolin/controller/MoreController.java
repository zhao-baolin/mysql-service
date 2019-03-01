package com.mysql.zhaobaolin.controller;

import com.mysql.zhaobaolin.entity.User;
import com.mysql.zhaobaolin.handler.CurdHandler;
import com.mysql.zhaobaolin.handler.MoreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:zhao-baolin
 * @Description:
 * @Date:Created 2018/9/18
 */
@Controller
@RequestMapping("/More")
public class MoreController {

    @Autowired
    private MoreHandler moreHandler;


    @RequestMapping("/find")
    @ResponseBody
    public String find()
    {
        String out = moreHandler.find();
        return out.toString();
    }

    @RequestMapping("/more")
    @ResponseBody
    public String more()
    {
        String out = moreHandler.more();
        return out.toString();
    }

}
