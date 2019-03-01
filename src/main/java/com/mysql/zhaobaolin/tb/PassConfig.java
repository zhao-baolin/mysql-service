package com.mysql.zhaobaolin.tb;


public class PassConfig {

    private static String tb = "tb_pass";
    private static String userId = "user_id";
    private static String password = "password";
    private static String payPassword = "pay_password";
    private static String createTime = "create_time";
    private static String updateTime = "update_time";

    public static String getTb() {
        return tb;
    }

    public static void setTb(String tb) {
        PassConfig.tb = tb;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        PassConfig.userId = userId;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        PassConfig.password = password;
    }

    public static String getPayPassword() {
        return payPassword;
    }

    public static void setPayPassword(String payPassword) {
        PassConfig.payPassword = payPassword;
    }

    public static String getCreateTime() {
        return createTime;
    }

    public static void setCreateTime(String createTime) {
        PassConfig.createTime = createTime;
    }

    public static String getUpdateTime() {
        return updateTime;
    }

    public static void setUpdateTime(String updateTime) {
        PassConfig.updateTime = updateTime;
    }
}
