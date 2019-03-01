package com.mysql.zhaobaolin.handler;

import com.mysql.zhaobaolin.entity.AppFxLog;
import com.mysql.zhaobaolin.entity.User;
import com.mysql.zhaobaolin.entity.UserMenu;
import com.mysql.zhaobaolin.entity.UserRole;
import com.mysql.zhaobaolin.service.MysqlService;
import com.mysql.zhaobaolin.tb.MenuConfig;
import com.mysql.zhaobaolin.tb.RoleConfig;
import com.mysql.zhaobaolin.tb.RuleConfig;
import com.mysql.zhaobaolin.tb.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhao-baolin
 * @Description: 各种查询操作
 * @Date:Created 2018/9/2
 */
@Component
public class SelectHandler {

    @Autowired
    private MysqlService mysqlService;

    //where查询
    public String where()
    {
        //可以直接写入sql条件
        //List<User> userList = mysqlService.name(UserConfig.getTb()).where("id=2").to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE id=2

        //以键值对调用where方法 支持连续调用and扩充条件
        //List<User> userList2 = mysqlService.name(UserConfig.getTb()).where(UserConfig.getId(),"3").and(UserConfig.getAge(),"33").to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE id=3 AND age=33

        //以键值对调用where方法 支持连续调用and扩充条件
/*        List<User> userList = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getRoleId(),"3")
                .and(UserConfig.getAge(),"28")
                .and(UserConfig.getStatus(),1)
                .to(User.class).select();*/
        //执行的sql为：SELECT * FROM tb_user WHERE role_id = 3 AND age =28 AND status =1

        //也可以使用map进行查询
        HashMap map = new HashMap();
        map.put(UserConfig.getRoleId(),3);
        map.put(UserConfig.getAge(),28);
        //List<User> userList3 = mysqlService.name(UserConfig.getTb()).where(map).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE id=3 AND role_id = 2 AND age = 33

        //也可以使用map进行查询
        HashMap map1 = new HashMap();
        map1.put(UserConfig.getRoleId(),22);
        map1.put(UserConfig.getAge(),18);
/*        List<User> userList5 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getStatus(),1)
                .and(map1)
                .to(User.class).select();*/
        //执行的sql为：SELECT * FROM tb_user WHERE status = 1 AND role_id = '22' AND age = '18'


        //也可以使用map进行外层and里层or查询
        List<User> userList4 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getId(),"3")
                .andOr(map)
                .to(User.class)
                .select();
        //执行的sql为：SELECT * FROM tb_user WHERE id=3 AND (role_id = 2 OR age = 33)


        System.out.println(userList4.toString());
        return userList4.toString();
    }

    //or条件查询
    public String or()
    {
        //where与or配合调用 同样支持连续调用
        List<User> userList1 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getRoleId(),"3")
                .or(UserConfig.getAge(),"18")
                .or(UserConfig.getStatus(),"1")
                .to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE role_id=3 OR age=18 OR status=1

        //也可以使用map进行外层or里层and查询
        HashMap map = new HashMap();
        map.put(UserConfig.getRoleId(),2);
        map.put(UserConfig.getAge(),33);
        List<User> userList2 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getId(),"3")
                .orAnd(map)
                .to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE id=3 OR (role_id = 2 AND age = 33)


        //更复杂的混搭查询
        HashMap map2 = new HashMap();
        map2.put(UserConfig.getStatus(),1);
        map2.put(UserConfig.getUserName(),"周杰伦");
        List<User> userList5 = mysqlService.name(UserConfig.getTb()).andOr(map).orAnd(map2).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE (role_id = '2' OR age = '33') OR (user_name = '周杰伦' AND status = '1')

        List<User> userList6 = mysqlService.name(UserConfig.getTb()).andOr(map).andOr(map2).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE (role_id = '2' OR age = '33') AND (user_name = '周杰伦' OR status = '1')


        System.out.println(userList2.toString());
        return userList2.toString();
    }

    public String like()
    {
        List<User> list = mysqlService.name(UserConfig.getTb()).like(UserConfig.getUserName(),"杰").select();
        System.out.println(mysqlService.getSQL());
        System.out.println(list.toString());

        List<User> list2 = mysqlService.name(UserConfig.getTb()).likeLeft(UserConfig.getUserName(),"学友").select();
        System.out.println(mysqlService.getSQL());
        System.out.println(list2.toString());

        List<User> list3 = mysqlService.name(UserConfig.getTb()).likeRight(UserConfig.getUserName(),"林").select();
        System.out.println(mysqlService.getSQL());
        System.out.println(list3.toString());
        return list.toString();
    }

    //表达式查询
    public String exp()
    {
        //简单查询
        List<User> userList1 = mysqlService.name(UserConfig.getTb()).where(UserConfig.getAge(),">",22.6).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE age > 22.6

        List<User> userList2 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getAge(),">",22.6)
                .and(UserConfig.getUserName(),"!=","林俊杰")
                .to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE age > 22.6 AND user_name != '林俊杰'

        List<User> userList3 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getAge(),">",22.6)
                .or(UserConfig.getId(),"<",10)
                .to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user WHERE age > 22.6 OR id < 10

        //如果你实在要折腾我 那我也提供andStart()、orStart()、end()三个方法手动开启和关闭条件块
        //HashMap map = new HashMap();
        //map.put(UserConfig.getRoleId(),2);
        //map.put(UserConfig.getAge(),33);
        List<User> userList4 = mysqlService.name(UserConfig.getTb())
                .where(UserConfig.getId(),"3")
                .andStart()
                .where(UserConfig.getRoleId(),"<",5)
                .or(UserConfig.getRoleId(),">",10)
                .end()
                .and(UserConfig.getStatus(),1)
                .to(User.class)
                .select();
        //执行的sql为：SELECT * FROM tb_user WHERE id = 3 AND (role_id < 5 OR role_id > 10) AND status =1
        //注：子条件开启后第一个条件请使用where()



        System.out.println(userList1.toString());
        return userList1.toString();
    }


    public String in()
    {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        List<User> list = mysqlService.name(UserConfig.getTb()).in(UserConfig.getId(),ids).select();
        System.out.println(list.toString());


/*        int s = mysqlService.name(UserConfig.getTb()).in(UserConfig.getId(),ids).setInc(UserConfig.getAge(),10).update();
        System.out.println(mysqlService.getSQL());
        System.out.println(s);*/

        List<User> list2 = mysqlService.name(UserConfig.getTb()).notIn(UserConfig.getId(),ids).select();
        System.out.println(list2.toString());

        return list.toString();
    }
    //查询指定条数数据
    public String limit()
    {
        List<User> userList2 = mysqlService.name(UserConfig.getTb()).limit(2,1).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user limit 2,1

        List<User> userList1 = mysqlService.name(UserConfig.getTb()).limit(2).to(User.class).select();
        // 执行的sql为：SELECT * FROM tb_user limit 2

        System.out.println(userList2.toString());
        return userList2.toString();
    }


    public String join()
    {

        //单表连接
        List<UserRole> list = mysqlService.name(UserConfig.getTb())
                .preField(UserConfig.getTb(),UserConfig.getId(),UserConfig.getUserName())
                .preField(RoleConfig.getTb(),RoleConfig.getName())
                .innerJoin(RoleConfig.getTb(),RoleConfig.getId(),UserConfig.getRoleId())
                .preWhere(UserConfig.getTb(),UserConfig.getStatus(),1)
                .preWhere(RoleConfig.getTb(),RoleConfig.getCreateId(),1)
                .to(UserRole.class)
                .select();

        //执行的sql为：SELECT tb_user.id,tb_user.user_name,tb_role.name FROM tb_user INNER JOIN tb_role ON tb_role.id = tb_user.role_id WHERE tb_user.status = 1 AND tb_role.create_id = 1


        // name(tb,'a').innerJoin(tb,'b'),on('b',id)
        //preField('tb')


        //与别名配合查询
/*
        List<Map> map1 = mysqlService.name(UserConfig.getTb(),"u")
                .innerJoin(RoleConfig.getTb(),"r")
                .on("r."+RoleConfig.getId(),"u."+UserConfig.getRoleId())
                .where("r."+RoleConfig.getStatus(),1)
                .and("u."+UserConfig.getStatus(),1)
                .select();
*/

        //你也可以写成下面这样
/*        List<Map> map2 = mysqlService.name(UserConfig.getTb(),"u")
                .innerJoin("tb_role r ON r.id = u.role_id")
                .where("r.status = 1 AND u.status =1")
                .select();*/
        //执行的sql为：SELECT * FROM tb_user u INNER JOIN tb_role r ON r.id = u.role_id WHERE r.status = 1 AND u.status =1


       //多表连接
/*
        List<UserMenu> map3 = mysqlService.name(MenuConfig.getTb(),"m")
                .field("u."+UserConfig.getUserName(),"m."+MenuConfig.getName(),"m."+MenuConfig.getUrl())
                .innerJoin(RuleConfig.getTb(),"r")
                .on("r."+RuleConfig.getMenuId(),"m."+MenuConfig.getId())
                .on("r."+RuleConfig.getStatus(),1)
                .innerJoin(UserConfig.getTb(),"u")
                .on("u."+UserConfig.getRoleId(),"r."+RuleConfig.getRoleId())
                .where("u."+UserConfig.getId(),2)
                .and("u."+UserConfig.getStatus(),1)
                .and("m."+MenuConfig.getStatus(),1)
                .to(UserMenu.class)
                .select();
*/

        //你也可以写成下面这样
/*        List<UserMenu> map4 = mysqlService.name(MenuConfig.getTb(),"m")
                .field("u.user_name,m.name,m.url")
                .innerJoin("tb_rule r ON r.menu_id = m.id AND r.status = 1")
                .innerJoin("tb_user u ON u.role_id = r.role_id")
                .where("u.id = 2 AND u.status =1 AND m.status =1")
                .to(UserMenu.class)
                .select();*/
/* 执行的sql为：
        SELECT u.user_name,m.name,m.url
        FROM tb_menu m
        INNER JOIN tb_rule r ON r.menu_id = m.id AND r.status = 1
        INNER JOIN tb_user u ON u.role_id = r.role_id
        WHERE u.id = 2 AND u.status =1 AND m.status =1
*/

        System.out.println(list.toString());
        return list.toString();
    }

    public String order()
    {
        //正序
        List<User> list0 = mysqlService.name(UserConfig.getTb()).order(UserConfig.getAge()).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user ORDER BY age ASC

        //正序
        List<User> list1 = mysqlService.name(UserConfig.getTb()).orderAsc(UserConfig.getAge()).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user ORDER BY age ASC

        //倒序
        List<User> list2 = mysqlService.name(UserConfig.getTb()).orderDesc(UserConfig.getAge()).to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user ORDER BY age DESC

        //多个排序
        List<User> list3 = mysqlService.name(UserConfig.getTb()).order("age desc,id desc").to(User.class).select();
        //执行的sql为：SELECT * FROM tb_user ORDER BY age desc,id desc

        List<User> list4 = mysqlService.name(UserConfig.getTb()).orderAsc(UserConfig.getAge()).orderDesc(UserConfig.getId()).to(User.class).select();

        //SELECT * FROM tb_user ORDER BY age ASC,id DESC

        System.out.println(list4.toString());
        return list4.toString();
    }

    public String group()
    {
        List<Map> maps = mysqlService.name(UserConfig.getTb()).field(UserConfig.getRoleId()).group(UserConfig.getRoleId()).select();
        //执行的sql为：SELECT role_id FROM tb_user GROUP BY role_id

        //多个分组
        List<Map> maps2 = mysqlService.name(UserConfig.getTb())
                .field(UserConfig.getRoleId())
                .group(UserConfig.getRoleId(),UserConfig.getAge())
                .select();
        //执行的sql为：SELECT role_id FROM tb_user GROUP BY role_id,age

        System.out.println(maps.toString());
        return maps.toString();
    }

    public String count()
    {

        Integer count = mysqlService.name(UserConfig.getTb()).where(UserConfig.getRoleId(),2).count();
        //执行的sql为：SELECT COUNT(*) FROM tb_user WHERE role_id = 2


        Integer count1 = mysqlService.name(MenuConfig.getTb(),"m")
                .innerJoin("tb_rule r ON r.menu_id = m.id AND r.status = 1")
                .innerJoin("tb_user u ON u.role_id = r.role_id")
                .where("u.id = 2 AND u.status =1 AND m.status =1")
                .count();
/* 执行的sql为：
SELECT COUNT(*) FROM tb_menu m
INNER JOIN tb_rule r ON r.menu_id = m.id AND r.status = 1
INNER JOIN tb_user u ON u.role_id = r.role_id
WHERE u.id = 2 AND u.status =1 AND m.status =1
*/
        System.out.println(count1);
        return count1.toString();
    }

    public String sql()
    {
        //独孤九剑 满足一切

        //查询sql调用query
        List<User> users = mysqlService.to(User.class).query("select * from tb_user where user_name='周杰伦'");

        //操作sql调用execute  insert/update/delete
        int s = mysqlService.execute("UPDATE tb_user SET role_id=3 WHERE id=5");

        mysqlService.getSQL();
        mysqlService.getError();
        System.out.println(users.toString());
        return users.toString();
    }

}
