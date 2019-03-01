package com.mysql.zhaobaolin.controller;

import com.mysql.zhaobaolin.handler.CurdHandler;
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
@RequestMapping("/Curd")
public class CurdController {

    @Autowired
    private CurdHandler curdHandler;

    //写入数据
    @RequestMapping("/insertGetId")
    @ResponseBody
    public String insertGetId()
    {
        Long out = curdHandler.insertGetId();
        return out.toString();
    }

    //写入数据
    @RequestMapping("/insert")
    @ResponseBody
    public String insert()
    {
        Integer out = curdHandler.insert();
        return out.toString();
    }

    //写入数据
    @RequestMapping("/insertMap")
    @ResponseBody
    public String insertMap()
    {
        Integer out = curdHandler.insertMap();
        return out.toString();
    }

    //更新数据
    @RequestMapping("/update")
    @ResponseBody
    public String update()
    {
        Integer out = curdHandler.update();
        return out.toString();
    }

    //更新数据
    @RequestMapping("/transaction")
    @ResponseBody
    public String transaction()
    {
        Integer out = curdHandler.transaction();
        return out.toString();
    }

    //删除数据
    @RequestMapping("/delete")
    @ResponseBody
    public String delete()
    {
        Integer out = curdHandler.delete();
        return out.toString();
    }

    //查询全表数据
    @RequestMapping("/select")
    @ResponseBody
    public String select()
    {
        String out = curdHandler.select();
        return out;
    }

    //查询单条数据
    @RequestMapping("/find")
    @ResponseBody
    public String find()
    {
        String out = curdHandler.find();
        return out;
    }

    //查询单条数据
    @RequestMapping("/field")
    @ResponseBody
    public String field()
    {
        String out = curdHandler.field();
        return out;
    }

}
