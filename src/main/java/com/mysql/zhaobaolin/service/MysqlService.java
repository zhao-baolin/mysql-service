package com.mysql.zhaobaolin.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * mysql统一操作类
 * zhaobaolin
 * 文档地址：https://www.kancloud.cn/zhaobl5201314/mysql_service/752845
 */
@Component
public class MysqlService<T> {
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static Lock writeLock = readWriteLock.writeLock();
    private String TABLE = ""; //表名
    private String AS = ""; //别名
    private String JOIN = ""; //表连接
    private String SQL_TYPE = ""; //sql类型
    private String WHERE = ""; //条件
    private String ORDER = ""; //排序
    private String GROUP = ""; //分组
    private String FIELD = "*"; //字段
    private String UPDATE_FIELD = ""; //更新内容
    private String DATA = ""; //新增数据
    private String LIMIT = "";//limit
    private Class TO = null;//转化对象
    private String SQL = ""; //sql语句
    private String SQL_STEP = "";//步长语句

    private String raw_field = "";//原始字段
    private String raw_limit = " limit ";//原始limit
    private Object raw_data = null;
    private Object raw_table = null;
    private HashMap raw_map = null;
    private String left_border = " (";
    private String right_border = ")";
    private String word_and = " AND ";
    private String word_or = " OR ";
    private String word_on = " ON ";
    private String word_inner_join = " INNER JOIN ";
    private String word_left_join = " LEFT JOIN ";
    private String word_right_join = " RIGHT JOIN ";
    private String word_full_join = " FULL JOIN ";
    private String word_order = " ORDER BY ";
    private String word_group = " GROUP BY ";
    private String word_from = " FROM ";
    private boolean hand_start = false;//是否手动开启条件块
    private boolean first_on = true;//是否第一次on条件
    private boolean step_on = false;//是否开启步长更新
    private boolean db_assign = false;//是否连接其他数据库
    private boolean db_assign_clean = true;//其他数据库连接是否已清理
    private boolean db_start = false;//是否开启其他库连接块

    private String create_time = "create_time";//字段名
    private String update_time = "update_time";//字段名
    private boolean time_auto = true;//自动更新时间字段
    private String insert_value = "";
    private String insert_field = "";

    private String word_value = "VALUE";
    private String word_values = "VALUES";
    private String word_tb = "Tb";
    private String word_id = "Id";
    private String word_create_time = "CreateTime";
    private String word_update_time = "UpdateTime";
    private String word_get = "get";
    private String word_count = "COUNT(*)";
    private String word_select = "SELECT ";
    private String word_insert = "INSERT INTO ";
    private String word_update = "UPDATE ";
    private String word_delete = "DELETE";
    private String word_set = " SET ";
    private String word_where = " WHERE ";
    private String where_exp = "^(>|<|=|<>|>=|<=|!=)$";//表达式正则校验
    private String hand_reg = "^(insert|update|delete|)$";//操作sql正则校验

    private String ERROR = null;//错误内容

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DriverManagerDataSource dataSource;

    @Value("${spring.datasource.driverClassName}")
    private String dbDriver;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.debug:false}")
    private boolean debug;

    //指定连接其他库
    public MysqlService db(String url,String userName,String password)
    {
        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            ERROR = "数据库连接参数错误";
            return null;
        }
        connect(url,userName,password);
        db_assign = true;
        db_assign_clean = false;
        return this;
    }

    //开启其他库连接
    public void dbStart(String url,String userName,String password)
    {
        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            ERROR = "数据库连接参数错误";
            return;
        }
        connect(url,userName,password);
        db_start = true;
    }

    //结束其他库连接
    public void dbEnd()
    {
        cleanDb();
        db_assign_clean = false;
        db_start = false;
        checkConnect();
    }

    //查询条数
    public Integer count()
    {
        SQL_TYPE = word_select;
        FIELD = word_count;
        TO = null;
        List<Object> objects = executeSelect();
        if(null == objects || 1 != objects.size()){
            return 0;
        }
        Map map = (Map)objects.get(0);
        Long lo = (Long)map.get(word_count);
        return lo.intValue();
    }

    //查询列表数据
    public List<Object> select()
    {
        SQL_TYPE = word_select;
        return executeSelect();
    }

    //查询列表数据
    public List<Object> select(Class cla)
    {
        SQL_TYPE = word_select;
        if(!StringUtils.isEmpty(cla)){
            TO = cla;
        }
        return executeSelect();
    }

    //查询单条数据
    public Object find()
    {
        return executeFind();
    }

    //查询单条数据
    public Object find(Class cla)
    {
        if(!StringUtils.isEmpty(cla)){
            TO = cla;
        }
        return executeFind();
    }

    //查询sql执行
    public List<Object> query(String sql)
    {
        checkConnect();
        String sqlStr = leftTrim(sql);//去空格
        String start = sqlStr.substring(0,6).toLowerCase();//截前六位转小写
        if(!"select".equals(start)){
            System.out.println("查询语句错误："+sql);
            unLock();
            return null;
        }
        SQL = sqlStr;
        filter();
        printSql();
        List<Object> list = new ArrayList<>();
        try{
            List<Map<String, Object>> content = jdbcTemplate.queryForList(SQL);
            cleanDb();
            for(Map<String,Object> linkedHashMap : content){
                if(null == TO){
                    list.add(linkedHashMap);
                }else{
                    list.add(mapToObj(linkedHashMap,TO));
                }
            }
        }catch (Exception e){
            ERROR = e.getMessage();
        }finally {
            unLock();
        }
        return list;
    }

    //操作sql执行
    public int execute(String sql)
    {
        lock();
        checkConnect();
        String sqlStr = leftTrim(sql);//去空格
        String start = sqlStr.substring(0,6).toLowerCase();//截前六位转小写
        if(!start.matches(hand_reg)){
            ERROR = "执行语句错误："+sql;
            unLock();
            return 0;
        }
        TO = Integer.class;
        SQL = sqlStr;
        return executeUpdate();
    }

    //获取最后一条自增id的值
    public Long getLastId()
    {
        lock();
        List<Object> list = query("SELECT LAST_INSERT_ID()");
        if(null != list && list.size() > 0){
            Map map = (Map) list.get(0);
            BigInteger bigInteger = (BigInteger) map.get("LAST_INSERT_ID()");
            return bigInteger.longValue();
        }
        return 0L;
    }
    //获取最后一次执行的sql语句
    public String getSQL() {
        return SQL;
    }

    //获取错误信息
    public String getError() {
        return ERROR;
    }

    //表配置
    public MysqlService table(Class tb_class)
    {
        lock();
        clean();
        if(!StringUtils.isEmpty(tb_class)){
            try{
                raw_table = tb_class.newInstance();
            }catch (Exception e){
                ERROR = e.getMessage();
            }
        }
        return this;
    }

    //表配置
    public MysqlService table(Object tb_obj)
    {
        if (tb_obj instanceof String) {
            ERROR = "不能设置表名";
            return this;
        }
        lock();
        clean();
        if(!StringUtils.isEmpty(tb_obj)){
            raw_table = tb_obj;
        }
        return this;
    }

    //设置表名
    public MysqlService name(String tb_name)
    {
        lock();
        clean();
        if(StringUtils.isEmpty(TABLE) && !StringUtils.isEmpty(tb_name)){
            TABLE = tb_name;
        }
        return this;
    }

    //设置表名 别名
    public MysqlService name(String tb_name,String as)
    {
        lock();
        clean();
        if(StringUtils.isEmpty(TABLE) && !StringUtils.isEmpty(tb_name) && !StringUtils.isEmpty(as)){
            TABLE = tb_name;
            AS = as;
        }
        return this;
    }

    //on
    public MysqlService on(String on)
    {
        if(!StringUtils.isEmpty(on)){
            JOIN += word_on + on;
            first_on = false;
        }
        return this;
    }

    //on
    public MysqlService on(String left,T right)
    {
        if(!StringUtils.isEmpty(left) && !StringUtils.isEmpty(right)){

            String v = "";
            // 1. on a.id=b.uid  2.on a.id=3  3.on a.name="宝琳"
            if(isNumeric(right.toString()) || right.toString().contains(".")){
                v = left+" = "+right+"";
            }else{
                v = left+" = '"+right+"'";
            }
            if(first_on){
                JOIN += word_on + v;
            }else{
                JOIN += word_and + v;
            }
            first_on = false;
        }
        return this;
    }

    //条件
    public MysqlService where(String w)
    {
        if(StringUtils.isEmpty(WHERE)){
            WHERE = w;
        }
        return this;
    }

    //条件
    public MysqlService where(String k,T v)
    {
        if(!StringUtils.isEmpty(k)){
            String str = "";
            String and = "";
            if(!StringUtils.isEmpty(WHERE) && !hand_start){
                and = " AND ";
            }
            if(null == v){
                str = and + k+" is null";
            }else{
                if(isNumeric(v.toString())){
                    str = and + k+" = "+v+"";
                }else{
                    str = and + k+" = '"+v+"'";
                }
            }
            WHERE += str;
        }
        return this;
    }

    //条件
    public MysqlService where(HashMap map)
    {
        if(map.size()>0){
            int i = 0;
            for (Object key : map.keySet()) {
                if(i > 0){
                    WHERE += " AND ";
                }
                WHERE += key+" = '"+map.get(key)+"'";
                i ++;
            }
        }
        return this;
    }

    //条件 - 表达式
    public MysqlService where(String k,String exp,T v)
    {
        if(!checkExp(exp)){
            ERROR = "非法表达式";
            unLock();
            return this;
        }

        if(!StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String str = "";
            String and = "";
            if(!StringUtils.isEmpty(WHERE) && !hand_start){
                and = " AND ";
            }
            if(isNumeric(v.toString())){
                str = and + k+" "+exp+" "+v+"";
            }else{
                str = and + k+" "+exp+" '"+v+"'";
            }
            WHERE += str;
        }
        return this;
    }


    //条件
    public MysqlService preWhere(String tb,String name,T value)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(name)){
            String str = "";
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            if(null == value){
                str = and + tb + "." + name + " is null";
            }else{
                if(isNumeric(value.toString())){
                    str = and + tb + "." + name + " = "+value+"";
                }else{
                    str = and + tb + "." + name + " = '"+value+"'";
                }
            }
            WHERE += str;
        }
        return this;
    }

    //条件
    public MysqlService preWhere(String tb,HashMap map)
    {
        String and = "";
        if(!StringUtils.isEmpty(tb) && map.size()>0){
            int i = 0;
            for (Object key : map.keySet()) {
                if(i > 0 || !StringUtils.isEmpty(WHERE)){
                    and = " AND ";
                }
                WHERE += and + tb + "." + key+" = '"+map.get(key)+"'";
                i ++;
            }
        }
        return this;
    }

    //条件 - 表达式
    public MysqlService preWhere(String tb,String name,String exp,T value)
    {
        if(!checkExp(exp)){
            ERROR = "非法表达式";
            unLock();
            return this;
        }

        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)){
            String str = "";
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            if(isNumeric(value.toString())){
                str = and + tb + "." + name + " "+exp+" "+value+"";
            }else{
                str = and + tb + "." + name + " "+exp+" '"+value+"'";
            }
            WHERE += str;
        }
        return this;
    }


    //like查询
    public MysqlService like(String k,String v)
    {
        if(!StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + k + " LIKE '%"+v+"%'";
        }
        return this;
    }

    //like查询
    public MysqlService preLike(String tb,String k,String v)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + tb + "." + k + " LIKE '%"+v+"%'";
        }
        return this;
    }

    public MysqlService likeLeft(String k,String v)
    {
        if(!StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + k + " LIKE '%"+v+"'";
        }
        return this;
    }

    public MysqlService preLikeLeft(String tb,String k,String v)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + tb + "." + k + " LIKE '%"+v+"'";
        }
        return this;
    }

    public MysqlService likeRight(String k,String v)
    {
        if(!StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + k + " LIKE '"+v+"%'";
        }
        return this;
    }

    public MysqlService preLikeRight(String tb,String k,String v)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String and = "";
            if(!StringUtils.isEmpty(WHERE)){
                and = " AND ";
            }
            WHERE += and + tb + "." + k + " LIKE '"+v+"%'";
        }
        return this;
    }

    //and条件
    public MysqlService and(String k,T v)
    {
        if(!StringUtils.isEmpty(k)){
            String str = "";
            String or = " AND ";
            if(null == v){
                str = or + k+" is null";
            }else{
                if(isNumeric(v.toString())){
                    str = or + k+" ="+v+"";
                }else{
                    str = or + k+" ='"+v+"'";
                }
            }
            WHERE += str;
        }
        return this;
    }

    //and条件 - 表达式
    public MysqlService and(String k,String exp,T v)
    {
        if(!checkExp(exp)){
            ERROR = "非法表达式";
            unLock();
            return this;
        }

        if(!StringUtils.isEmpty(k) && null != v){
            String str = "";
            String and = " AND ";
            if(isNumeric(v.toString())){
                str = and + k+" "+exp+" "+v+"";
            }else{
                str = and + k+" "+exp+" '"+v+"'";
            }
            WHERE += str;
        }
        return this;
    }

    //and条件
    public MysqlService and(HashMap map)
    {
        if(map.size()>0){
            int i = 0;
            for (Object key : map.keySet()) {
                WHERE += " AND ";
                WHERE += key+" = '"+map.get(key)+"'";
                i ++;
            }
        }
        return this;
    }

    //or条件
    public MysqlService or(String k,T v)
    {
        if(!StringUtils.isEmpty(k)){
            String str = "";
            String or = " OR ";
            if(null == v){
                str = or + k+" is null";
            }else{
                if(isNumeric(v.toString())){
                    str = or + k+" "+v+"";
                }else{
                    str = or + k+" '"+v+"'";
                }
            }
            WHERE += str;
        }
        return this;
    }

    //or条件 - 表达式
    public MysqlService or(String k,String exp,T v)
    {
        if(!checkExp(exp)){
            ERROR = "非法表达式";
            unLock();
            return this;
        }

        if(!StringUtils.isEmpty(k) && !StringUtils.isEmpty(v)){
            String str = "";
            String and = " OR ";
            if(isNumeric(v.toString())){
                str = and + k+" "+exp+" "+v+"";
            }else{
                str = and + k+" "+exp+" '"+v+"'";
            }
            WHERE += str;
        }
        return this;
    }

    //or条件
    public MysqlService or(HashMap map)
    {
        if(map.size()>0){
            int i = 0;
            for (Object key : map.keySet()) {
                WHERE += " OR ";
                WHERE += key+" = '"+map.get(key)+"'";
                i ++;
            }
        }
        return this;
    }

    //or条件
    public MysqlService orAnd(HashMap map)
    {
        if(map.size()>0){
            int i = 0;
            if(StringUtils.isEmpty(WHERE)){
                WHERE = "(";
            }else{
                WHERE += " OR (";
            }
            for (Object key : map.keySet()) {
                if(i > 0){
                    WHERE += " AND ";
                }
                WHERE += key+" = '"+map.get(key)+"'";
                i ++;
            }
            WHERE += ")";
        }
        return this;
    }

    //and条件
    public MysqlService andOr(HashMap map)
    {
        if(map.size()>0){
            int i = 0;
            if(StringUtils.isEmpty(WHERE)){
                WHERE = "(";
            }else{
                WHERE += " AND (";
            }
            for (Object key : map.keySet()) {
                if(i > 0){
                    WHERE += " OR ";
                }
                WHERE += key+" = '"+map.get(key)+"'";
                i ++;
            }
            WHERE += ")";
        }
        return this;
    }

    //in
    public MysqlService in(String k,List<Long> ins)
    {
        if(!StringUtils.isEmpty(k)){
            String str = " IN (";
            String and = "";
            if(!StringUtils.isEmpty(WHERE) && !hand_start){
                and = " AND ";
            }
            boolean is = false;
            for(Long in : ins){
                is = true;
                str += in.toString() + ",";
            }

            if(is){
                str = sub(str);
                str = str + ") ";
                String in = and + k + str;
                WHERE += in;
            }
        }
        return this;
    }

    //not In
    public MysqlService notIn(String k,List<Long> ins)
    {
        if(!StringUtils.isEmpty(k)){
            String str = " NOT IN (";
            String and = "";
            if(!StringUtils.isEmpty(WHERE) && !hand_start){
                and = " AND ";
            }
            boolean is = false;
            for(Long in : ins){
                is = true;
                str += in.toString() + ",";
            }

            if(is){
                str = sub(str);
                str = str + ") ";
                String in = and + k + str;
                WHERE += in;
            }
        }
        return this;
    }


    //设置字段
    public MysqlService field(String f)
    {
        if(!StringUtils.isEmpty(f)){
            raw_field = f;
            FIELD = f;
        }
        return this;
    }

    //设置字段
    public MysqlService field(String ...str)
    {
        if(!StringUtils.isEmpty(str)){
            FIELD = "";
            for(String s : str){
                FIELD += s +",";
            }
            FIELD = sub(FIELD);
        }
        return this;
    }

    //设置字段
    public MysqlService preField(String tb,String ...str)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(str)){
            if("*".equals(FIELD)){
                FIELD = "";
            }else {
                FIELD += ",";
            }
            for(String s : str){
                FIELD += tb+"."+s +",";
            }
            FIELD = sub(FIELD);
        }
        return this;
    }

    public MysqlService data(Object obj)
    {
        raw_data = obj;
        return this;
    }

    public MysqlService data(HashMap map)
    {
        if(!StringUtils.isEmpty(map)){
            raw_map = map;
        }
        return this;
    }

    public MysqlService data(String key,T value)
    {
        if(!StringUtils.isEmpty(key)){
            HashMap map = new HashMap();
            map.put(key,value);
            raw_map = map;
        }
        return this;
    }

    private void preInsert()
    {
        if(null != raw_map){
            FIELD = "";
            for (Object key : raw_map.keySet()) {
                FIELD += key+",";
                if(null == raw_map.get(key)){
                    DATA += null+",";
                }else{
                    Object param = raw_map.get(key);
                    if (param instanceof Integer) {
                        int value = ((Integer) param).intValue();
                        DATA += value+",";
                    } else if (param instanceof String) {
                        String s = (String) param;
                        DATA += "'"+s+"',";
                    } else if (param instanceof Double) {
                        double d = ((Double) param).doubleValue();
                        DATA += d+",";
                    } else if (param instanceof Float) {
                        float f = ((Float) param).floatValue();
                        DATA += f+",";
                    } else if (param instanceof Long) {
                        long l = ((Long) param).longValue();
                        DATA += l+",";
                    } else if (param instanceof Boolean) {
                        boolean b = ((Boolean) param).booleanValue();
                        DATA += b+",";
                    } else if (param instanceof BigDecimal) {
                        BigDecimal dec = (BigDecimal) param;
                        DATA += dec+",";
                    }
                }
            }
        }else{
            if(null == raw_data || null == raw_table){
                if(debug){
                    System.out.println("表配置对象不存在或插入数据不存在");
                }
                return;
            }

            Field[] field = raw_table.getClass().getDeclaredFields();
            // 遍历所有属性
            for (int j = 0; j < field.length; j++) {
                if(0 == j){
                    FIELD = "";
                }

                // 获取属性的名字
                String name = field[j].getName();
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

                String tbField = null;
                String tbData = null;
                try{
                    Method m1 = raw_table.getClass().getMethod(word_get + name);
                    tbField = (String)m1.invoke(raw_table);
                    if(StringUtils.isEmpty(tbField)){
                        continue;
                    }
                    if(word_tb.equals(name)){
                        TABLE = tbField;
                        continue;
                    }
                    if(word_id.equals(name)){
                        continue;
                    }

                    Method m2 = raw_data.getClass().getMethod(word_get + name);
                    Object d = m2.invoke(raw_data);
                    if(null == d){
                        continue;
                    }
                    tbData = d.toString();

                    if(name.equals(word_create_time) && "0".equals(tbData)){
                        continue;
                    }
                    if(name.equals(word_update_time) && "0".equals(tbData)){
                        continue;
                    }

                }catch (Exception e){
                    if(debug){
                        System.out.println("出错了"+e.getMessage());
                    }
                    ERROR = e.getMessage();
                }

                if(!StringUtils.isEmpty(tbData)){
                    FIELD += tbField+",";
                    DATA += "'"+tbData+"',";
                }
            }

            //自动插入时间
            if(!StringUtils.isEmpty(FIELD) && time_auto){
                Long now = System.currentTimeMillis();
                if(!FIELD.matches("(.*)"+create_time+"(.*)")){
                    FIELD += create_time+",";
                    DATA += now+",";
                }
                if(!FIELD.matches("(.*)"+update_time+"(.*)")){
                    FIELD += update_time+",";
                    DATA += now+",";
                }
            }
        }
        if(StringUtils.isEmpty(TABLE)){
            System.out.println("表名不存在");
        }
        FIELD = sub(FIELD);
        DATA = sub(DATA);
    }

    //新增数据返回自增id
    public Long insertGetId()
    {
        preInsert();
        if(StringUtils.isEmpty(TABLE) || StringUtils.isEmpty(FIELD) || StringUtils.isEmpty(DATA)){
            clean();
            unLock();
            return 0L;
        }
        insertSplice();
        checkConnect();
        filter();
        Long id = 0L;
        printSql();
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(SQL
                            , Statement.RETURN_GENERATED_KEYS);
                    return ps;
                }
            }, keyHolder);
            id = keyHolder.getKey().longValue();
        }catch (Exception e){
            ERROR = e.getMessage();
        }finally {
            cleanDb();
            TO = null;
            unLock();
        }
        return id;
    }

    //新增数据
    public int insert()
    {
        preInsert();
        if(StringUtils.isEmpty(TABLE) || StringUtils.isEmpty(FIELD) || StringUtils.isEmpty(DATA)){
            clean();
            unLock();
            return 0;
        }

        insertSplice();
        TO = Integer.class;
        Integer d =  executeUpdate();
        return d;
    }

    public int update()
    {
        if(null != raw_map){
            UPDATE_FIELD = "";
            for (Object key : raw_map.keySet()) {

                Object param = raw_map.get(key);
                if (param instanceof Integer) {
                    int value = ((Integer) param).intValue();
                    DATA += value+",";
                    UPDATE_FIELD += key+"="+value+",";
                } else if (param instanceof String) {
                    String s = (String) param;
                    UPDATE_FIELD += key+"='"+s+"',";
                } else if (param instanceof Double) {
                    double d = ((Double) param).doubleValue();
                    UPDATE_FIELD += key+"="+d+",";
                } else if (param instanceof Float) {
                    float f = ((Float) param).floatValue();
                    UPDATE_FIELD += key+"="+f+",";
                } else if (param instanceof Long) {
                    long l = ((Long) param).longValue();
                    UPDATE_FIELD += key+"="+l+",";
                } else if (param instanceof Boolean) {
                    boolean b = ((Boolean) param).booleanValue();
                    UPDATE_FIELD += key+"="+b+",";
                } else if (param instanceof BigDecimal) {
                    BigDecimal dec = (BigDecimal) param;
                    UPDATE_FIELD += key+"="+dec+",";
                }
            }
        }else if(null != raw_data && null != raw_table){

            Field[] field = raw_table.getClass().getDeclaredFields();
            // 遍历所有属性
            for (int j = 0; j < field.length; j++) {
                if(0 == j){
                    UPDATE_FIELD = "";
                }
                // 获取属性的名字
                String name = field[j].getName();
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

                String tbField = null;
                String tbData = null;
                try{
                    Method m1 = raw_table.getClass().getMethod(word_get + name);
                    tbField = (String)m1.invoke(raw_table);
                    if(StringUtils.isEmpty(tbField)){
                        continue;
                    }
                    if(word_tb.equals(name)){
                        TABLE = tbField;
                        continue;
                    }
                    if(word_id.equals(name)){
                        continue;
                    }

                    Method m2 = raw_data.getClass().getMethod(word_get + name);
                    tbData = m2.invoke(raw_data).toString();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

                if(!StringUtils.isEmpty(tbData)){
                    UPDATE_FIELD += tbField+"='"+tbData+"',";
                }
            }
        }

        if(StringUtils.isEmpty(TABLE)){
            ERROR = "找不到表名";
            clean();
            unLock();
            return 0;
        }

        //步长更新
        if(step_on) {
            UPDATE_FIELD += SQL_STEP;
        }

        if(StringUtils.isEmpty(UPDATE_FIELD)){
            ERROR = "找不到更新内容";
            clean();
            unLock();
            return 0;
        }

        //自动更新时间
        if(!StringUtils.isEmpty(UPDATE_FIELD) && time_auto && !UPDATE_FIELD.matches("(.*)"+update_time+"(.*)")){
            Long now = System.currentTimeMillis();
            UPDATE_FIELD += update_time+"="+now+",";
        }

        UPDATE_FIELD = sub(UPDATE_FIELD);
        updateSplice();
        TO = Integer.class;
        Integer d =  executeUpdate();
        return d;
    }

    public int delete()
    {
        if(StringUtils.isEmpty(TABLE)){
            ERROR = "找不到表名";
            clean();
            unLock();
            return 0;
        }

        if(StringUtils.isEmpty(WHERE)){
            ERROR = "找不到删除条件";
            clean();
            unLock();
            return 0;
        }

        deleteSplice();
        TO = Integer.class;
        Integer d =  executeUpdate();
        return d;
    }

    public int deleteAll()
    {
        if(StringUtils.isEmpty(TABLE)){
            ERROR = "找不到表名";
            clean();
            unLock();
            return 0;
        }
        deleteSplice();
        TO = Integer.class;
        Integer d =  executeUpdate();
        return d;
    }

    //设置自动更新时间字段
    public MysqlService autoTime(boolean auto)
    {
        if(!auto){
            time_auto = false;
        }
        return this;
    }

    //limit
    public MysqlService limit(Integer limit)
    {
        if(!StringUtils.isEmpty(limit) && limit > 0){
            LIMIT = raw_limit+limit.toString();
        }
        return this;
    }

    //limit x,y
    public MysqlService limit(Integer begin,Integer limit)
    {
        if(!StringUtils.isEmpty(begin) && !StringUtils.isEmpty(limit) && limit > 0){
            LIMIT = raw_limit+begin.toString()+","+limit.toString();
        }
        return this;
    }

    //order 默认asc
    public MysqlService order(String order)
    {
        if(!StringUtils.isEmpty(order)){
            String type = "";
            if(!order.matches("(.*),(.*)")){
                type = " ASC";
            }
            ORDER = word_order + order + type;
        }
        return this;
    }

    //升序
    public MysqlService orderAsc(String order)
    {
        if(!StringUtils.isEmpty(order)){
            String str = word_order;
            if(!StringUtils.isEmpty(ORDER)){
                str = ",";//连续调用
            }
            ORDER += str + order + " ASC";
        }
        return this;
    }

    //升序
    public MysqlService preOrderAsc(String tb,String order)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(order)){
            String str = word_order;
            if(!StringUtils.isEmpty(ORDER)){
                str = ",";//连续调用
            }
            ORDER += str + tb + "." + order + " ASC";
        }
        return this;
    }

    //降序
    public MysqlService orderDesc(String order)
    {
        if(!StringUtils.isEmpty(order)){
            String str = word_order;
            if(!StringUtils.isEmpty(ORDER)){
                str = ",";//连续调用
            }
            ORDER += str + order+" DESC";
        }
        return this;
    }

    //降序
    public MysqlService preOrderDesc(String tb,String order)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(order)){
            String str = word_order;
            if(!StringUtils.isEmpty(ORDER)){
                str = ",";//连续调用
            }
            ORDER += str + tb + "." + order+" DESC";
        }
        return this;
    }

    //group by
    public MysqlService group(String group)
    {
        if(!StringUtils.isEmpty(group)){
            GROUP = word_group+group;
        }
        return this;
    }

    //group by
    public MysqlService preGroup(String tb,String group)
    {
        if(!StringUtils.isEmpty(tb) && !StringUtils.isEmpty(group)){
            GROUP = word_group+tb+"."+group;
        }
        return this;
    }

    //group by
    public MysqlService group(String ...group)
    {
        if(!StringUtils.isEmpty(group)){
            GROUP = word_group;
            for(String s : group){
                GROUP += s +",";
            }
            GROUP = sub(GROUP);
        }
        return this;
    }

    //转换目标对象
    public MysqlService to(Class cla)
    {
        if(!StringUtils.isEmpty(cla)){
            TO = cla;
        }
        return this;
    }

    //手动开启and条件块
    public MysqlService andStart()
    {
        if(StringUtils.isEmpty(WHERE)){
            WHERE = left_border;
        }else{
            WHERE += word_and + left_border;
        }
        hand_start = true;
        return this;
    }

    //手动开启or条件块
    public MysqlService orStart()
    {
        if(StringUtils.isEmpty(WHERE)){
            WHERE = left_border;
        }else{
            WHERE += word_or + left_border;
        }
        hand_start = true;
        return this;
    }

    //结束条件块
    public MysqlService end()
    {
        if(!StringUtils.isEmpty(WHERE)){
            WHERE += right_border;
        }
        hand_start = false;
        return this;
    }

    //增长步长1
    public MysqlService setInc(String k)
    {
        setInc(k,1);
        return this;
    }
    //增长步长自定义
    public MysqlService setInc(String k,Integer num)
    {
        if(!StringUtils.isEmpty(k) && 0 < num){
            SQL_STEP += k+"="+k+"+"+num+",";
            step_on = true;
        }
        return this;
    }
    //增长步长自定义
    public MysqlService setInc(String k,BigDecimal num)
    {
        if(!StringUtils.isEmpty(k) && BigDecimal.ZERO.compareTo(num) == -1){
            SQL_STEP += k+"="+k+"+"+num+",";
            step_on = true;
        }
        return this;
    }
    //增长步长自定义
    public MysqlService setInc(String k,Double num)
    {
        if(!StringUtils.isEmpty(k) && BigDecimal.ZERO.compareTo(new BigDecimal(num)) == -1){
            SQL_STEP += k+"="+k+"+"+num+",";
            step_on = true;
        }
        return this;
    }
    //增长步长1
    public MysqlService setDec(String k)
    {
        setDec(k,1);
        return this;
    }
    //减少步长自定义
    public MysqlService setDec(String k,Integer num)
    {
        if(!StringUtils.isEmpty(k) && 0 < num){
            SQL_STEP += k+"="+k+"-"+num+",";
            step_on = true;
        }
        return this;
    }
    //减少步长自定义
    public MysqlService setDec(String k,BigDecimal num)
    {
        if(!StringUtils.isEmpty(k) && BigDecimal.ZERO.compareTo(num) == -1){
            SQL_STEP += k+"="+k+"-"+num+",";
            step_on = true;
        }
        return this;
    }
    //减少步长自定义
    public MysqlService setDec(String k,Double num)
    {
        if(!StringUtils.isEmpty(k) && BigDecimal.ZERO.compareTo(new BigDecimal(num)) == -1){
            SQL_STEP += k+"="+k+"-"+num+",";
            step_on = true;
        }
        return this;
    }

    //leftJoin
    public MysqlService leftJoin(String t)
    {
        if(!StringUtils.isEmpty(t)){
            JOIN += word_left_join + t;
            first_on = true;
        }
        return this;
    }

    //leftJoin
    public MysqlService leftJoin(String join,String as)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(as)){
            JOIN += word_left_join + join + " "+as;
            first_on = true;
        }
        return this;
    }

    //leftJoin
    public MysqlService leftJoin(String join,String join_field,String table_field)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(join_field) && !StringUtils.isEmpty(table_field)){
            String jtb = StringUtils.isEmpty(AS) ? TABLE : AS;
            JOIN += word_left_join + join + word_on + join+"."+join_field + " = " + jtb +"."+ table_field;
            first_on = true;
        }
        return this;
    }

    //innerjoin
    public MysqlService innerJoin(String t)
    {
        if(!StringUtils.isEmpty(t)){
            JOIN += word_inner_join + t;
            first_on = true;
        }
        return this;
    }

    //innerjoin
    public MysqlService innerJoin(String join,String as)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(as)){
            JOIN += word_inner_join + join + " "+as;
            first_on = true;
        }
        return this;
    }

    //innerjoin
    public MysqlService innerJoin(String join,String join_field,String table_field)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(join_field) && !StringUtils.isEmpty(table_field)){
            String jtb = StringUtils.isEmpty(AS) ? TABLE : AS;
            JOIN += word_inner_join + join + word_on + join+"."+join_field + " = " + jtb +"."+ table_field;
            first_on = true;
        }
        return this;
    }

    //rightJoin
    public MysqlService rightJoin(String t)
    {
        if(!StringUtils.isEmpty(t)){
            JOIN += word_right_join + t;
            first_on = true;
        }
        return this;
    }

    //rightJoin
    public MysqlService rightJoin(String join,String as)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(as)){
            JOIN += word_right_join + join + " "+as;
            first_on = true;
        }
        return this;
    }

    //rightJoin
    public MysqlService rightJoin(String join,String join_field,String table_field)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(join_field) && !StringUtils.isEmpty(table_field)){
            String jtb = StringUtils.isEmpty(AS) ? TABLE : AS;
            JOIN += word_right_join + join + word_on + join+"."+join_field + " = " + jtb +"."+ table_field;
            first_on = true;
        }
        return this;
    }

    //fullJoin
    public MysqlService fullJoin(String t)
    {
        if(!StringUtils.isEmpty(t)){
            JOIN += word_full_join + t;
            first_on = true;
        }
        return this;
    }

    //fullJoin
    public MysqlService fullJoin(String join,String join_field,String table_field)
    {
        if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(join_field) && !StringUtils.isEmpty(table_field)){
            String jtb = StringUtils.isEmpty(AS) ? TABLE : AS;
            JOIN += word_full_join + join + word_on + join+"."+join_field + " = " + jtb +"."+ table_field;
            first_on = true;
        }
        return this;
    }

    //开启事务
    public void startTrans()
    {
        String str = "START TRANSACTION";
        jdbcTemplate.execute(str);
    }

    //回滚
    public void rollback()
    {
        String str = "ROLLBACK";
        jdbcTemplate.execute(str);
    }

    //提交
    public void commit()
    {
        String str = "COMMIT";
        jdbcTemplate.execute(str);
    }

    //去除字符串左边空格
    private String leftTrim(String str)
    {
        if (str == null || "".equals(str)) {
            return str;
        } else {
            return str.replaceAll("^[　 ]+", "");
        }
    }

    //执行
    private List<Object> executeSelect()
    {
        checkConnect();
        selectSplice();
        printSql();
        List<Map<String, Object>> content = null;
        try{
            content = jdbcTemplate.queryForList(SQL);
        }catch (Exception e){
            ERROR = e.getMessage();
        }finally {
            unLock();
        }
        if(null == content){
            return null;
        }

        cleanDb();
        List<Object> list = new ArrayList<>();
        for(Map<String,Object> linkedHashMap : content){
            if(null == TO){
                list.add(linkedHashMap);
            }else{
                list.add(mapToObj(linkedHashMap,TO));
            }
        }
        return list;
    }

    //执行
    private Object executeFind()
    {
        checkConnect();
        findMust();
        selectSplice();
        printSql();
        Map<String,Object> content = null;
        try{
            content = jdbcTemplate.queryForMap(SQL);
        }catch (Exception e){
            ERROR = e.getMessage();
        }finally {
            cleanDb();
            unLock();
        }

        if(null == TO){
            //没有指定转换对象
            if(StringUtils.isEmpty(raw_field)){
                return content;
            }else{
                return content.get(raw_field);
            }
        }else{
            return mapToObj(content,TO);
        }
    }

    //执行
    private int executeUpdate()
    {
        checkConnect();
        filter();
        int row = 0;
        printSql();
        try{
            row = jdbcTemplate.update(SQL);
        }catch (Exception e){
            ERROR = e.getMessage();
        }finally {
            cleanDb();
            TO = null;
            unLock();
        }

        return row;
    }

    //单条查询必要条件
    private void findMust()
    {
        SQL_TYPE = word_select;
        LIMIT = raw_limit+"1";
    }

    //map转对象
    private Object mapToObj(Map map,Class cla)
    {
        String object = JSONObject.toJSONString(map);
        Object obj = JSONObject.parseObject(object,cla);
        return obj;
    }

    //分析条件
    private void parseWhere()
    {
        WHERE = !StringUtils.isEmpty(WHERE) ? word_where + WHERE : "";
    }

    //判断字符串是否由数字组成
    private boolean isNumeric(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

    //校验表达式是否合法
    private boolean checkExp(String str){
        return str.matches(where_exp);
    }

    //校验字符串中是否含有小数点
    private boolean checkDian(String str){
        return str.matches("\\.");
    }

    //字段去除末位
    private String sub(String str)
    {
        if(StringUtils.isEmpty(str)){
            return "";
        }
        return str.substring(0,str.length()-1);
    }

    //组装select sql
    private void selectSplice()
    {
        parseWhere();
        TABLE = StringUtils.isEmpty(AS) ? TABLE : TABLE + " AS " + AS;
        SQL = SQL_TYPE + FIELD + word_from + TABLE + JOIN + WHERE + GROUP + ORDER + LIMIT;
        filter();
    }

    //组装insert sql
    private void insertSplice()
    {
        insert_value = word_value+left_border+DATA+right_border;
        insert_field = left_border+FIELD+right_border;
        SQL = word_insert+TABLE+insert_field+" "+insert_value;
    }

    //组装update sql
    private void updateSplice()
    {
        parseWhere();
        SQL = word_update+TABLE+word_set+UPDATE_FIELD+WHERE;
    }

    //组装delete sql
    private void deleteSplice()
    {
        parseWhere();
        SQL = word_delete + word_from + TABLE + WHERE;
    }


    //调试模式打印sql
    private void printSql()
    {
        if(debug){
            System.out.println("执行sql："+SQL);
        }
    }

    //sql安全过滤
    private void filter(){
        if(StringUtils.isEmpty(SQL)){
            return;
        }
        SQL = SQL.replace("/*","");
        SQL = SQL.replace("*/","");
        SQL = SQL.replaceAll("(?:#|--)", "");
    }

    //数据库连接
    private void connect(String url,String userName,String password)
    {
        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            ERROR = "数据库连接参数错误";
            return;
        }
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        jdbcTemplate = new JdbcTemplate();
        // 这里也可以使用构造方法
        jdbcTemplate.setDataSource(dataSource);
    }

    //清理条件 避免污染下次查询
    private void clean()
    {
        TABLE = ""; //表名
        AS = ""; //别名
        JOIN = ""; //表连接
        SQL_TYPE = ""; //sql类型
        WHERE = ""; //条件
        ORDER = ""; //排序
        GROUP = ""; //分组
        FIELD = "*"; //字段
        UPDATE_FIELD = ""; //更新内容
        DATA = ""; //新增数据
        LIMIT = "";//limit
        TO = null;//转化对象
        SQL = ""; //sql语句
        SQL_STEP = "";//步长语句

        raw_field = "";//原始字段
        raw_limit = " limit ";//原始limit
        raw_data = null;
        raw_table = null;
        raw_map = null;

        hand_start = false;//是否手动开启条件块
        first_on = true;//是否第一次on条件
        step_on = false;//是否开启步长更新
        time_auto = true;//自动更新时间字段
        insert_value = "";
        insert_field = "";
        ERROR = null;
    }

    //获取锁
    private void lock()
    {
        writeLock.lock();
    }

    //释放锁
    private void unLock()
    {
        writeLock.unlock();
    }

    //恢复正常连接
    private void cleanDb()
    {
        db_assign = false;
    }

    //校验数据库连接
    private void checkConnect()
    {
        //数据库未切换 && 其他数据库连接未清理 && 连接块未打开 则恢复默认连接
        if(!db_assign && !db_assign_clean && !db_start){
            connect(dbUrl,dbUserName,dbPassword);
            db_assign_clean = true;
        }
    }

}
