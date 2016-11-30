package cn.itcast.goods.cart.domain;

import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.user.domain.User;

import java.math.BigDecimal;

/**
 * Created by yanke on 2016/11/29.
 */
public class CartItem {
    private String cartItemId;
    private int quantity;  //数量
    private Book book;  //条目对应图书
    private User user;  //所属用户
    /**
     * 小计方法
     * @return
     */
    public double getSubtotal(){
        BigDecimal b1 = new BigDecimal(book.getCurrPrice()+"");
        BigDecimal b2 = new BigDecimal(quantity+"");//只能是String构造器保证精度
        BigDecimal b3 = b1.multiply(b2);  //不能用*

        return b3.doubleValue();
    }
    public static void main(String[] args) {
        System.out.println(2.0-1.1); //0.8999999
    } //BigDecimal BigInteger

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
