package com.mysql.zhaobaolin.entity;


import java.io.Serializable;

public class AppFxLog implements Serializable{

    private long id;
    private long userId;
    private long fromUser;
    private long relation;
    private long orderId;
    private String remark;
    private String createAt;
    private long status;
    private double fxMoney;
    private long type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFromUser() {
        return fromUser;
    }

    public void setFromUser(long fromUser) {
        this.fromUser = fromUser;
    }

    public long getRelation() {
        return relation;
    }

    public void setRelation(long relation) {
        this.relation = relation;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public double getFxMoney() {
        return fxMoney;
    }

    public void setFxMoney(double fxMoney) {
        this.fxMoney = fxMoney;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AppFxLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", fromUser=" + fromUser +
                ", relation=" + relation +
                ", orderId=" + orderId +
                ", remark='" + remark + '\'' +
                ", createAt='" + createAt + '\'' +
                ", status=" + status +
                ", fxMoney=" + fxMoney +
                ", type=" + type +
                '}';
    }
}
