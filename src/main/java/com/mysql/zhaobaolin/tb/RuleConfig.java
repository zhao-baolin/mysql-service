package com.mysql.zhaobaolin.tb;


public class RuleConfig {

    private static String tb = "tb_rule";
    private static String id = "id";
    private static String roleId = "role_id";
    private static String menuId = "menu_id";
    private static String status = "status";

    public static String getTb() {
        return tb;
    }

    public static void setTb(String tb) {
        RuleConfig.tb = tb;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        RuleConfig.id = id;
    }

    public static String getRoleId() {
        return roleId;
    }

    public static void setRoleId(String roleId) {
        RuleConfig.roleId = roleId;
    }

    public static String getMenuId() {
        return menuId;
    }

    public static void setMenuId(String menuId) {
        RuleConfig.menuId = menuId;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        RuleConfig.status = status;
    }
}
