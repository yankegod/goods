package cn.itcast.goods.order.domain;

import cn.itcast.goods.book.domain.Book;

/**
 * Created by yanke on 2016/12/1.
 */
public class OrderItem {
    private  String orderItemId;
    private int quantity;
    private double subtotal;
    private Book book;  //所关联的book 不能只写bid，若不卖这本书，只用bid就会发生主外键错误
    private Order order; //所属的订单

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
