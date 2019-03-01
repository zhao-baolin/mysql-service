package com.mysql.zhaobaolin.controller;

import com.mysql.zhaobaolin.handler.SelectHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:zhao-baolin
 * @Description:
 * @Date:Created 2018/9/1
 */
@Controller
@RequestMapping("/Select")
public class SelectController {

    @Autowired
    private SelectHandler selectHandler;

    //where查询
    @RequestMapping("/where")
    @ResponseBody
    public String where()
    {
        String out = selectHandler.where();
        return out;
    }

    //or查询
    @RequestMapping("/or")
    @ResponseBody
    public String or()
    {
        String out = selectHandler.or();
        return out;
    }

    //like查询
    @RequestMapping("/like")
    @ResponseBody
    public String like()
    {
        String out = selectHandler.like();
        return out;
    }

    //in查询
    @RequestMapping("/in")
    @ResponseBody
    public String in()
    {
        String out = selectHandler.in();
        return out;
    }

    //表达式查询
    @RequestMapping("/exp")
    @ResponseBody
    public String exp()
    {
        String out = selectHandler.exp();
        return out;
    }

    //limit查询
    @RequestMapping("/limit")
    @ResponseBody
    public String limit()
    {
        String out = selectHandler.limit();
        return out;
    }

    //join查询
    @RequestMapping("/join")
    @ResponseBody
    public String join()
    {
        String out = selectHandler.join();
        return out;
    }

    //order查询
    @RequestMapping("/order")
    @ResponseBody
    public String order()
    {
        String out = selectHandler.order();
        return out;
    }

    //order查询
    @RequestMapping("/group")
    @ResponseBody
    public String group()
    {
        String out = selectHandler.group();
        return out;
    }

    //sql查询
    @RequestMapping("/sql")
    @ResponseBody
    public String sql()
    {
        String out = selectHandler.sql();
        return out;
    }

    //count查询
    @RequestMapping("/count")
    @ResponseBody
    public String count()
    {
        String out = selectHandler.count();
        return out;
    }

}
