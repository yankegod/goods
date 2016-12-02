package cn.itcast.goods.order.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.page.Expression;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.page.PageConstants;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yanke on 2016/12/1.
 */
public class OrderDao {
    private QueryRunner queryRunner = new TxQueryRunner();

    /**
     * 查询所有记录分页设置PageBean
     * @param expressionsList
     * @param pc
     * @return
     * @throws SQLException
     */
    private PageBean<Order> findByCriteria(List<Expression> expressionsList,int pc) throws SQLException {
        int ps = PageConstants.ORDER_PAGE_SIZE;//每页记录数

        //通过expression来生成where子句  ,和bookDao方法相同

        StringBuilder whereSql =new StringBuilder(" where 1=1 ");
        List<Object> params  = new ArrayList<Object>(); //对应SQl中的？
        for(Expression expressions :expressionsList){
            whereSql.append(" and ").append(expressions.getName()).append(" ")
                    .append(expressions.getOperator()).append("");
            if(!expressions.getOperator().equals("is null")){
                whereSql.append("?");
                params.add(expressions.getValue());
            }
        }
        //总记录数
        String sql = "select count(*) from t_order"+whereSql;
        Number number = (Number)queryRunner.query(sql,new ScalarHandler(),params.toArray());  //参数转化成数组
        int tr = number.intValue();//总记录数
        //得到ListBean 即当前页记录
        sql = "select * from t_order "+ whereSql + " order by ordertime desc limit ?,?";
        params.add((pc-1)*ps);//当前页首行记录的下标
        params.add(ps);//每页记录数

        //怎么把uid找出来？？？
        List<Order> beanList = queryRunner.query(sql,new BeanListHandler<Order>(Order.class)
                             ,params.toArray());

        //虽然获取了每个订单但订单中没有订单条目，遍历每个订单加载订单条目

        for (Order order:beanList){
            loadOrderItem(order);
        }

        //创建pageBean,设置参数
        PageBean<Order> pageBean = new PageBean<Order>();
        pageBean.setBeanList(beanList);
        pageBean.setPc(pc);
        pageBean.setPs(ps);
        pageBean.setTr(tr);

        return pageBean;
    }

    /**
     * 为order加载他的所有orderItem
     * @param order
     */
    private void loadOrderItem(Order order) throws SQLException {
        //有book的内容要用Map来完成映射
        String sql = "select * from t_orderitem where oid=?";
        List<Map<String,Object>> mapList= queryRunner.query(sql,new MapListHandler(),order.getOid());
        List<OrderItem> orderItemList = toOrderItemList(mapList);

        order.setOrderItemList(orderItemList);
    }

    /**
     * 把多个Map转换成多个OrderItem
     * @param mapList
     * @return
     */
    private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {

        List<OrderItem> orderItemList =new ArrayList<OrderItem>();
        for(Map<String,Object> map :mapList){
            OrderItem orderItem = toOrderItem(map);
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 把一个map转换成一个OrderItem，需要再映射出一个book
     * @param map
     * @return
     */
    private OrderItem toOrderItem(Map<String, Object> map) {

        OrderItem orderItem =CommonUtils.toBean(map,OrderItem.class);
        Book book = CommonUtils.toBean(map,Book.class);
        orderItem.setBook(book);    //注意吧book set到里面！！

        return orderItem;
    }

    /**
     * 查询订单状态
     * @param oid
     * @return
     * @throws SQLException
     */
    public int findStatus(String oid) throws SQLException {
        String sql = "select status from t_order where oid =?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),oid);

        return number.intValue();  //需要返回一个int类型的值
    }

    /**
     * 修改订单状态
     * @param oid
     * @param status
     * @throws SQLException
     */
    public void updateStatus(String oid ,int status) throws SQLException {
        String sql = "update t_order set status = ? where oid = ?";
        queryRunner.update(sql,status,oid);
    }
    /**
     * 加载订单
     * @param oid
     * @return
     * @throws SQLException
     */
    public Order load(String oid) throws SQLException {
        String sql = "select * from t_order where oid = ?";

        //这种不会丢当前用户，但用户在session里面有
//        Map<String,Object> map= queryRunner.query(sql,new MapHandler(),oid);
//        Order order = CommonUtils.toBean(map,Order.class);
//        User user = CommonUtils.toBean(map,User.class);
//        order.setOwer(user);
        Order order = queryRunner.query(sql,new BeanHandler<Order>(Order.class),oid);

        loadOrderItem(order);  //为当前订单加载所有订单条目，双向关联。

        return order;

    }
    /**
     * 通过User查询订单数
      * @param uid
     * @param pc
     * @return
     */
    public PageBean<Order> findByUser(String uid,int pc) throws SQLException {

        List<Expression> expressionList = new ArrayList<Expression>();
        expressionList.add(new Expression("uid","=",uid));
        return findByCriteria(expressionList,pc);
    }

    /**
     * 生成订单：插入订单，插入订单项
     * @param order
     */
    public void add(Order order) throws SQLException {

        String sql ="insert into t_order values (?,?,?,?,?,?)";
        Object[] params ={order.getOid(),order.getOrdertime(),order.getTotal(),order.getStatus(),
                order.getAddress(),order.getOwer().getUid()};  //注意顺序和树木，数据库要的是用户的uid！！！
        queryRunner.update(sql,params);

        //循环遍历订单的所有条目每个条目是一个Objecct[],多个订单条目就是个Object[][]
        //执行批处理，完成所有条目的插入
        sql = "insert into t_orderitem values (?,?,?,?,?,?,?,?)";
        int len = order.getOrderItemList().size();

        Object[][] objects =new Object[len][];
        for(int i=0;i<len;i++){
            OrderItem orderItem =order.getOrderItemList().get(i);
            objects[i] = new Object[]{orderItem.getOrderItemId(),orderItem.getQuantity(),
                    orderItem.getSubtotal(),orderItem.getBook().getBid(),
                    orderItem.getBook().getBname(),orderItem.getBook().getCurrPrice(),
                    orderItem.getBook().getImage_b(),order.getOid()
            };

        }
        queryRunner.batch(sql,objects);    //执行批处理。。
    }

}
