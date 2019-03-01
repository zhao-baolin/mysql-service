package com.mysql.zhaobaolin.tb;


public class RoleConfig {

    private static String tb = "tb_role";
    private static String id = "id";
    private static String name = "name";
    private static String createId = "create_id";
    private static String status = "status";
    private static String createTime = "create_time";
    private static String updateTime = "update_time";

    public static String getTb() {
        return tb;
    }

    public static void setTb(String tb) {
        RoleConfig.tb = tb;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        RoleConfig.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        RoleConfig.name = name;
    }

    public static String getCreateId() {
        return createId;
    }

    public static void setCreateId(String createId) {
        RoleConfig.createId = createId;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        RoleConfig.status = status;
    }

    public static String getCreateTime() {
        return createTime;
    }

    public static void setCreateTime(String createTime) {
        RoleConfig.createTime = createTime;
    }

    public static String getUpdateTime() {
        return updateTime;
    }

    public static void setUpdateTime(String updateTime) {
        RoleConfig.updateTime = updateTime;
    }
}
