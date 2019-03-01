package com.mysql.zhaobaolin.handler;

import com.mysql.zhaobaolin.entity.User;
import com.mysql.zhaobaolin.service.MysqlService;
import com.mysql.zhaobaolin.tb.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhao-baolin
 * @Description: 数据库CURD操作
 * @Date:Created 2018/9/18
 */
@Component
public class MoreHandler {

    @Autowired
    private MysqlService mysqlService;

    //查询单条数据
    public String find()
    {
        String url = "jdbc:mysql://192.168.1.248:3306/plancoin_exchange_dev_022?useUnicode=true&characterEncoding=utf-8";
        String userName = "develop";
        String password = "develop^$zhongfan2018";

        Map map = (Map) mysqlService.db(url,userName,password).name("tb_usr_admins").find();
        System.out.println(map.toString());

        User user = (User)mysqlService.name(UserConfig.getTb()).to(User.class).find();
        System.out.println(user.toString());

        User user2 = (User)mysqlService.name(UserConfig.getTb()).to(User.class).find();
        System.out.println(user2.toString());

        Map map2 = (Map) mysqlService.db(url,userName,password).name("tb_usr_admins").find();
        System.out.println(map2.toString());


        Map map3 = (Map) mysqlService.db(url,userName,password).name("tb_usr_admins").find();
        System.out.println(map3.toString());

        User user6 = (User)mysqlService.name(UserConfig.getTb()).to(User.class).find();
        System.out.println(user6.toString());

        return user.toString();
    }


    public String more()
    {
        String url = "jdbc:mysql://192.168.1.248:3306/plancoin_exchange_dev_022?useUnicode=true&characterEncoding=utf-8";
        String userName = "develop";
        String password = "develop^$zhongfan2018";

        mysqlService.dbStart(url,userName,password);

        Map map = (Map) mysqlService.name("tb_usr_admins").find();
        System.out.println(map.toString());

        Map map2 = (Map) mysqlService.name("tb_usr_admins").find();
        System.out.println(map2.toString());

        mysqlService.dbEnd();

        User user = (User)mysqlService.name(UserConfig.getTb()).to(User.class).find();
        System.out.println(user.toString());



        return user.toString();
    }
}
