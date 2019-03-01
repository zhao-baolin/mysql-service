package com.mysql.zhaobaolin.entity;


import java.io.Serializable;

public class Rule implements Serializable {

    private long id;
    private long roleId;
    private long menuId;
    private long status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }


    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }


    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", menuId=" + menuId +
                ", status=" + status +
                '}';
    }
}
