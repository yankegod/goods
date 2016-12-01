package cn.itcast.goods.order.domain;

import cn.itcast.goods.user.domain.User;

import java.util.List;

/**
 * Created by yanke on 2016/12/1.
 */
public class Order {
    private String oid;//主键
    private String ordertime; //订单时间
    private double total;  //总计
    private int status; //1，未付款，2，已付款但未发货，3，已发货未确认收货 4，确认收货交易成功  5，已取消（只有未付款）
    private String address;//收货地址
    private User ower; //订单所有者

    private List<OrderItem> orderItemList; // 一个订单中有多个订单条目

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getOwer() {
        return ower;
    }

    public void setOwer(User ower) {
        this.ower = ower;
    }
}
