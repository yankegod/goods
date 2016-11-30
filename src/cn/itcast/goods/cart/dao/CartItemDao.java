package cn.itcast.goods.cart.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yanke on 2016/11/29.
 */

public class CartItemDao {
    private QueryRunner qr = new TxQueryRunner();

    /**
     * 生成where子句
     * @param len
     * @return
     */
    private String toWhereSql(int len){
        StringBuilder stringBuilder = new StringBuilder("cartItemId in(");
        for (int i = 0; i<len;i++){
            stringBuilder.append("?");
            if(i<len-1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    /**
     * 加载多个cartItem
     * @param cartItemIds
     * @return
     * @throws SQLException
     */
    public List<CartItem> loadCartItems(String cartItemIds) throws SQLException {

        Object[] cartItemIdArray = cartItemIds.split(",");//转换成数组

        String whereSql = toWhereSql(cartItemIdArray.length);  //生成where子句

        String sql ="select * from t_cartItem c,t_book b where c.bid = b.bid and "+whereSql;

         return toCartItemList(qr.query(sql,new MapListHandler(),cartItemIdArray));
    }
    /**
     * 按id查询，与图书关联，要多表查询
     * @param cartItemId
     *
     */
    public CartItem findByCartItemId(String cartItemId) throws SQLException {
        String sql = "select * from t_cartItem c,t_book b where c.bid=b.bid and c.cartItemId = ? ";
        Map<String ,Object> map = qr.query(sql,new MapHandler(),cartItemId);
        return toCartTtem(map);
    }
    /**
     * 批量删除
     * @param cartItemIds
     */
    public void batchDelete(String cartItemIds) throws SQLException {

        /*先把cartItemId转换成数组
         *把cartItem转换成一个where子句，与deletefrom结合，然后执行
        */
        Object[] cartItemIdArray = cartItemIds.split(",");

        String whereSql = toWhereSql(cartItemIdArray.length);

        String sql = "delete from t_cartItem where "+whereSql;

        qr.update(sql,cartItemIdArray);   //cartItemIdArray必须是Obeject类型数组，是update里需要可变参数
    }


    /**
     * 查询某个用户的某本图书是否存在
     * @param uid
     * @return
     */
    public CartItem findByUidAndBid(String uid,String bid) throws SQLException {
        String sql = "select * from t_cartitem where uid = ? and bid =?";
        Map<String,Object> map= qr.query(sql,new MapHandler(),uid,bid);
        CartItem cartItem = toCartTtem(map);
        return cartItem;
    }

    /**
     * 修改指定条目数量
     */
    public void updateQuantity(String cartItemId,int quantity) throws SQLException {
        String sql = "update t_cartitem set quantity = ? where cartItemId= ?";
        qr.update(sql,quantity,cartItemId);
    }

    /**
     * 添加条目
     * @param cartItem
     * @throws SQLException
     */
    public void addCartItem(CartItem cartItem) throws SQLException {
        String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid) values(?,?,?,?)";
        Object[] params ={cartItem.getCartItemId(),cartItem.getQuantity(),
                cartItem.getBook().getBid(),cartItem.getUser().getUid()};
        qr.update(sql,params);
    }
    /**
     * 把map映射成一个CartItem
     * @param map
     * @return
     */
    private CartItem toCartTtem(Map<String,Object> map){

        if (map == null || map.size() == 0)return null; //为啥不能用用&：java.lang.NullPointerException
                                                        //如果我的购物车为空，也会
                                                        //如果用户未登陆，往购物车加东西：java.lang.NullPointerException
        CartItem cartItem = CommonUtils.toBean(map,CartItem.class);
        Book book = CommonUtils.toBean(map,Book.class);
        User user = CommonUtils.toBean(map,User.class);

        cartItem.setBook(book);
        cartItem.setUser(user);

        return cartItem;
    }

    /**
     * 把多个Map()映射成多个CartItem
     * @param maplist
     * @return
     */
    private List<CartItem> toCartItemList(List<Map<String ,Object>> maplist){
        List<CartItem> cartItems = new ArrayList<CartItem>();

        for(Map<String,Object> map :maplist){
            CartItem cartItem = toCartTtem(map);
            cartItems.add(cartItem);
        }
        return cartItems;
    }
     /**
     * 通过用户查询购物车
     * @param uid
     * @return
     * @throws SQLException
     */
    public List<CartItem> findByUser(String uid) throws SQLException {
        String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and uid=? order by c.orderBy";

        List<Map<String,Object>> mapList = qr.query(sql,new MapListHandler(),uid);

        return toCartItemList(mapList);
    }


}
