package com.mysql.zhaobaolin.handler;

import com.mysql.zhaobaolin.entity.User;
import com.mysql.zhaobaolin.service.MysqlService;
import com.mysql.zhaobaolin.tb.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhao-baolin
 * @Description: 数据库CURD操作
 * @Date:Created 2018/9/1
 */
@Component
public class CurdHandler {

    @Autowired
    private MysqlService mysqlService;

    public Long insertGetId()
    {
        User user = new User();
        user.setId(1000);
        user.setUserName("测试人18");
        user.setAge(18);
        user.setPhone("15115115111");
        user.setRoleId(3);
        user.setStatus(1);
        Long s = mysqlService.table(UserConfig.class).data(user).insertGetId();
        System.out.println("返回的id是："+s);
        return s;
    }

    public Integer insert()
    {
        User user = new User();
        user.setId(1000);
        user.setUserName("测试人28");
        user.setAge(18);
        user.setPhone("15115115111");
        user.setRoleId(3);
        user.setStatus(1);
        Integer s = mysqlService.table(UserConfig.class).data(user).insert();
        Long d = mysqlService.getLastId();
        System.out.println("d是："+d);
        System.out.println("s是："+s);
        //insertGetId()

        //执行的sql为：INSERT INTO tb_user (user_name,age,phone,role_id,status,create_time,update_time)VALUE ('测试人员2','18','15115115111','3','1',1536290767867,1536290767867)    自动忽略id字段


        //也可以实例化对象后传入
//        UserConfig userConfig = new UserConfig();
//        userConfig.setStatus(null);//手动排除status字段
//        userConfig.setRoleId(null);//手动排除role_id字段
//        Integer f = mysqlService.table(userConfig).data(user).insert();
        //执行的sql为：INSERT INTO tb_user (user_name,age,phone,create_time,update_time)VALUE ('测试人员2','18','15115115111',1536292513728,1536292513728)

        //注：此两种写入方式自动忽略tb与id字段，但tb字段必须要指定表名
        return s;
    }

    public Integer insertMap()
    {
        //也可以使用map 此方式写入数据不会自动忽略id
        Long now = System.currentTimeMillis();
        HashMap map = new HashMap();
        map.put(UserConfig.getId(),30);
        map.put(UserConfig.getUserName(),"map测试");
        map.put(UserConfig.getCreateTime(),now);
        Integer d = mysqlService.name(UserConfig.getTb()).data(map).insert();
        if(0 == d){
            System.out.println(mysqlService.getError());
        }
        //执行的sql为：INSERT INTO tb_user (update_time,create_time,user_name,id ) VALUE ('1535876667524','1535876667524','map测试','10' )
        return d;
    }

    public Integer update()
    {
        //更新操作仅支持HashMap

        //自动更新update_time字段
        HashMap map = new HashMap();
        BigDecimal s = new BigDecimal("3");
        map.put(UserConfig.getAmount(),s);

        //Integer s = mysqlService.name(UserConfig.getTb()).data(map).where(UserConfig.getId(),11).update();
        //执行的sql为：UPDATE tb_user SET role_id='10',update_time=1536323304119 WHERE id = 11

        //覆盖自动更新
        HashMap map1 = new HashMap();
        map1.put(UserConfig.getRoleId(),10);
        map1.put(UserConfig.getUpdateTime(),2000000000);
        //Integer ss = mysqlService.name(UserConfig.getTb()).data(map1).where(UserConfig.getId(),11).update();
        //执行的sql为：UPDATE tb_user SET update_time='2000000000',role_id='10' WHERE id = 3

        //取消自动更新
        HashMap map2 = new HashMap();
        map2.put(UserConfig.getRoleId(),22);
        //Integer sss = mysqlService.name(UserConfig.getTb()).data(map2).where(UserConfig.getId(),11).autoTime(false).update();
        //执行的sql为：UPDATE tb_user SET role_id='22' WHERE id = 11


        //如果要针对数字步长更新 可以调用如下setInc setDec方法 不传步长则默认操作步长为1
        //Integer a = mysqlService.name(UserConfig.getTb()).where(UserConfig.getId(),3).setInc(UserConfig.getAge()).update();
        //执行的sql为：UPDATE tb_user SET age=age+1,update_time=1536219010993 WHERE id = 3

        //增长步长
        HashMap map3 = new HashMap();
        map3.put(UserConfig.getRoleId(),20);
        map3.put(UserConfig.getPhone(),"19119119111");
        //Integer aa = mysqlService.name(UserConfig.getTb()).where(UserConfig.getId(),3).data(map3).setInc(UserConfig.getAge(),20).update();
        //执行的sql为：UPDATE tb_user SET role_id='20',phone='19119119111',age=age+20,update_time=1536324490486 WHERE id = 3

        //减少步长
        Integer aaa = mysqlService.name(UserConfig.getTb()).data(UserConfig.getAge(),4).where(UserConfig.getId(),1).update();
        //执行的sql为：UPDATE tb_user SET age=age-20,update_time=1536220716206 WHERE id = 3

        return aaa;
    }


    public Integer transaction()
    {

        HashMap map = new HashMap();
        map.put(UserConfig.getRoleId(),3);
        mysqlService.startTrans();
        Integer s = mysqlService.name(UserConfig.getTb()).data(map).where(UserConfig.getId(),3).update();
        //执行的sql为：UPDATE tb_user SET role_id='10',update_time=1536205344392 WHERE id = 3



        mysqlService.rollback();

        return s;
    }

    public Integer delete()
    {
        Integer s = mysqlService.name(UserConfig.getTb()).where(UserConfig.getId(),10).delete();
        //执行的sql为：DELETE FROM tb_user WHERE id = 10

        System.out.println(s.toString());
        return s;
    }

    //查询数据集
    public String select()
    {
        List<User> userList = mysqlService.name(UserConfig.getTb()).to(User.class).select();

        //也可以不调用to方法
        List<User> userList2 = mysqlService.name(UserConfig.getTb()).select(User.class);

        //以上查询语句结果相同：SELECT * FROM tb_user

        System.out.println(userList2.toString());
        return userList2.toString();
    }

    //查询单条数据
    public String find()
    {
        //执行的sql为：SELECT * FROM tb_user limit 1
        User user = (User)mysqlService.name(UserConfig.getTb()).to(User.class).find();
        System.out.println(user.toString());
        return user.toString();
    }

    //查询指定字段
    public String field()
    {
        //查询单条数据 单/多个字段 赋值给对象
        User obj = (User)mysqlService.name(UserConfig.getTb()).field(UserConfig.getUserName(),UserConfig.getAge()).to(User.class).find();

        //也可以返回Map 返回Map的时候key为数据表原生字段名
        Map map = (Map) mysqlService.name(UserConfig.getTb()).field(UserConfig.getUserName(),UserConfig.getAge()).find();
        //System.out.println(map.get("user_name"));
        //执行的sql为：SELECT user_name,age FROM tb_user limit 1


        //查询单条数据 单个字段 可以直接转换成包装类型对象
        String userName = (String)mysqlService.name(UserConfig.getTb()).field(UserConfig.getUserName()).find();
        //执行的sql为：SELECT user_name FROM tb_user limit 1


        //查询多条数据 单/多个字段
        List<User> obj2 = mysqlService.name(UserConfig.getTb()).field(UserConfig.getUserName(),UserConfig.getAge()).to(User.class).select();

        //也可以返回List<Map>
        List<Map> obj3 = mysqlService.name(UserConfig.getTb()).field(UserConfig.getUserName(),UserConfig.getAge()).select();
        //执行的sql为：SELECT user_name,age FROM tb_user

        System.out.println(obj3.toString());
        return obj3.toString();
    }



}
