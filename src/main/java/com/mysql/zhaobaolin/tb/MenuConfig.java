package com.mysql.zhaobaolin.tb;


public class MenuConfig {

    private static String tb = "tb_menu";
    private static String id = "id";
    private static String name = "name";
    private static String url = "url";
    private static String parentId = "parent_id";
    private static String status = "status";
    private static String createTime = "create_time";
    private static String updateTime = "update_time";

    public static String getTb() {
        return tb;
    }

    public static void setTb(String tb) {
        MenuConfig.tb = tb;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        MenuConfig.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MenuConfig.name = name;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        MenuConfig.url = url;
    }

    public static String getParentId() {
        return parentId;
    }

    public static void setParentId(String parentId) {
        MenuConfig.parentId = parentId;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        MenuConfig.status = status;
    }

    public static String getCreateTime() {
        return createTime;
    }

    public static void setCreateTime(String createTime) {
        MenuConfig.createTime = createTime;
    }

    public static String getUpdateTime() {
        return updateTime;
    }

    public static void setUpdateTime(String updateTime) {
        MenuConfig.updateTime = updateTime;
    }
}
