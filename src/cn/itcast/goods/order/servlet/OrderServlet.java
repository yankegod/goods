package cn.itcast.goods.order.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yanke on 2016/12/1.
 */

@WebServlet("/OrderServlet" )
public class OrderServlet extends BaseServlet {
    private OrderService orderService = new OrderService();
    private CartItemService cartItemService = new CartItemService();//跨模块依赖
    /**
     * 获取当前页码,可借鉴book的处理
     * @param req
     * @return
     */
    private int getPc(HttpServletRequest req){
        int pc = 1;
        String param = req.getParameter("pc");
        if(param != null &&!param.trim().isEmpty()){
            try{
                pc =Integer.parseInt(param);
            }catch (RuntimeException e){}
        }
        return pc;
    }

    /**
     * 加载订单
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String load(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String oid =req.getParameter("oid");
        Order order =orderService.load(oid);
        req.setAttribute("order",order);  //req.getParameter() req.getAttribute()的区别
        String btn = (String) req.getParameter("btn");//确定用户用哪个超链接来访问本方法的。
        req.setAttribute("btn",btn);
        return "f:/jsps/order/desc.jsp";
    }
    /**
     * 创建订单
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String createOrder(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cartItemIds = req.getParameter("cartItemIds");
        //获取所有条目id，查询之。
        List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
        //创建Order
        Order order = new Order();
        order.setOid(CommonUtils.uuid());
        order.setOrdertime(String.format("%tF %<tT",new Date()));//下单时间
        order.setStatus(1);
        order.setAddress(req.getParameter("address"));

        User owner = (User) req.getSession().getAttribute("sessionUser");
        order.setOwer(owner);

        BigDecimal total = new BigDecimal("0");//只能用字符串类型构造保证精度
        for (CartItem cartItem:cartItemList){
            total = total.add(new BigDecimal(cartItem.getSubtotal()+""));
        }
        order.setTotal(total.doubleValue());

        //创建List<OrderItem>  一个CartItem对应一个OrderItem
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(CartItem cartItem:cartItemList){
            OrderItem orderItem =new OrderItem();
            orderItem.setOrderItemId(CommonUtils.uuid());
            orderItem.setQuantity(cartItem.getQuantity());  //购物车的东西是用来生成订单的
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(order);  //最上面的order

            orderItemList.add(orderItem);
        }
        order.setOrderItemList(orderItemList);
        //service完成添加
        orderService.createOders(order);
        //删除购物车条目
        cartItemService.batchDelete(cartItemIds);
        //保存订单
        req.setAttribute("order",order);

        return "f:/jsps/order/ordersucc.jsp";
    }

    /**
     * 获取URL
     * @param req
     * @return
     */
    private String getUrl(HttpServletRequest req){
        String url  = req.getRequestURI()+"?"+req.getQueryString();
        //如果url中有pc就截取，没有就不用截取
        int index = url.indexOf("&pc=");
        if (index != -1) {
            url = url.substring(0,index); //一定要赋给url
        }
        return url;
    }

    /**
     * 按用户查
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public String myOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int pc =getPc(req);
        String url = getUrl(req);
        //从当前sesison中获取用户信息，和book不同,只有登录的用户才能访问！！！

        User user = (User) req.getSession().getAttribute("sessionUser");

        PageBean<Order> pageBean = orderService.myOrders(user.getUid(),pc);

        pageBean.setUrl(url);
        req.setAttribute("pb",pageBean);

        return "f:/jsps/order/list.jsp";
    }
}
