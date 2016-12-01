package cn.itcast.goods.order.service;

import cn.itcast.goods.order.dao.OrderDao;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.page.PageBean;
import cn.itcast.jdbc.JdbcUtils;

import java.sql.SQLException;

/**
 * Created by yanke on 2016/12/1.
 */
public class OrderService {
    private OrderDao orderDao =new OrderDao();

    /**
     * 加载订单
     * @param uid
     * @return
     */
    public Order load(String uid){
        try {
            JdbcUtils.beginTransaction();
            Order order = orderDao.load(uid);
            JdbcUtils.commitTransaction();
            return order;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {}
            throw new RuntimeException(e);
        }
    }
    /**
     * 生成订单
     * @param order
     */
    public void createOders(Order order){
        try {
            //开启事务
            JdbcUtils.beginTransaction();
            orderDao.add(order);
            JdbcUtils.commitTransaction();

        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {}
            throw new RuntimeException(e);
        }
    }

    /**
     * 我的订单:事务处理，有两张表两个实体类
     * @param uid
     * @param pc
     * @return
     */
    public PageBean<Order> myOrders(String uid, int pc){
        try {
            //开启事务
            JdbcUtils.beginTransaction();
            PageBean<Order> pageBean =orderDao.findByUser(uid,pc);
            JdbcUtils.commitTransaction();
            return pageBean;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {}
            throw new RuntimeException(e);
        }
    }
}
