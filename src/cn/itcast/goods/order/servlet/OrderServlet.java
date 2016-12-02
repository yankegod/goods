package cn.itcast.goods.order.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.order.util.PaymentUtil;
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
import java.util.Properties;

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
     * 回馈方法，支付成功后易宝会访问这个方法：两种形式：
     * 1.引导用户浏览器重定向
     * 2.点对点的方式访问这个方法，必须返回success否则易宝服务器会一直调用这个方法
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String back(HttpServletRequest req ,HttpServletResponse resp)
            throws ServletException,IOException{
        //获取十二个参数,获取keyValue,调用PaymentUtil的校验方法，校验调用者的身份
        //校验失败，保存错误信息，转发到msg.jsp
        //校验通过：1：重定向：修改订单状态，保存信息，转发到msg.jsp  //可添加校验订单状态，防止恶意刷礼品。
        //        2.点对点：修改订单状态，返回success。
        String p1_MerId =req.getParameter("p1_MerId");
        String r0_Cmd =req.getParameter("r0_Cmd");
        String r1_Code =req.getParameter("r1_Code");
        String r2_TrxId =req.getParameter("r2_TrxId");
        String r3_Amt =req.getParameter("r3_Amt");
        String r4_Cur =req.getParameter("r4_Cur");
        String r5_Pid =req.getParameter("r5_Pid");
        String r6_Order =req.getParameter("r6_Order");
        String r7_Uid =req.getParameter("r7_Uid");
        String r8_MP =req.getParameter("r8_MP");
        String r9_BType =req.getParameter("r9_BType");
        String hmac =req.getParameter("hmac");

        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
        String keyValue = properties.getProperty("keyValue");

        Boolean bool = PaymentUtil.verifyCallback(hmac,p1_MerId,r0_Cmd,r1_Code,r2_TrxId,
                             r3_Amt,r4_Cur,r5_Pid,r6_Order,r7_Uid,r8_MP,r9_BType,keyValue);

        if(!bool){
            req.setAttribute("code","error");
            req.setAttribute("msg","无效签名，支付失败！！！");

            return "f:/jsps/msg.jsp";
        }

        if (r1_Code.equals("1")){
            orderService.updateStatus(r6_Order,2);
            if(r9_BType.equals("1")){
                req.setAttribute("code","success");
                req.setAttribute("msg","恭喜支付成功！！！");
            return "f:/jsps/msg.jsp";
            }else if (r9_BType.equals("2")){         //字符串注意双引号！！！
                resp.getWriter().print("success");
            }
        }
        return null;
    }

    /**
     * 支付方法
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String payment(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException {
        //准备13个参数
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));

        String  p0_Cmd   = "Buy"; //业务类型；
        String  p1_MerId = properties.getProperty("p1_MerId");  //商户编号，易宝的唯一标识，在配置文件中；
        String  p2_Order =req.getParameter("oid");//订单编码，要重定向，都是字符串。
        String  p3_Amt = "0.01";//支付金额。
        String  p4_Cur = "CNY";
        String  p5_Pid = "";//商品名称
        String  p6_Pcat =""; //商品种类
        String  p7_Pdesc = "";//商品描述
        String  p8_Url = properties.getProperty("p8_Url");//支付成功后易宝访问地址
        String  p9_SAF = ""; //送货地址
        String  pa_MP  = "";//扩展信息
        String  pd_FrpId = req.getParameter("yh"); //支付通道
        String  pr_NeedResponse = "1";//应答机制，固定值1
        /**
         * 计算hmac ：13个参数，keyValue ，加密算法
         */
        String keyValue = properties.getProperty("keyValue");
        System.out.println(p0_Cmd+p1_MerId+p2_Order+p3_Amt+p4_Cur+p5_Pid+p6_Pcat+p7_Pdesc+
                p8_Url+p9_SAF+pa_MP+pd_FrpId+pr_NeedResponse+keyValue);

        String hmac = PaymentUtil.buildHmac(p0_Cmd,p1_MerId,p2_Order,p3_Amt,p4_Cur,p5_Pid,p6_Pcat,p7_Pdesc,
                p8_Url,p9_SAF,pa_MP,pd_FrpId,pr_NeedResponse,keyValue);

        /**
         * 重定向到易宝支付网关
         */
        StringBuilder stringBuilder =new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
        stringBuilder.append("?").append("p0_Cmd=").append(p0_Cmd);
        stringBuilder.append("&").append("p1_MerId=").append(p1_MerId);
        stringBuilder.append("&").append("p2_Order=").append(p2_Order);
        stringBuilder.append("&").append("p3_Amt=").append(p3_Amt);
        stringBuilder.append("&").append("p4_Cur=").append(p4_Cur);
        stringBuilder.append("&").append("p5_Pid=").append(p5_Pid);
        stringBuilder.append("&").append("p6_Pcat=").append(p6_Pcat);
        stringBuilder.append("&").append("p7_Pdesc=").append(p7_Pdesc);
        stringBuilder.append("&").append("p8_Url=").append(p8_Url);
        stringBuilder.append("&").append("p9_SAF=").append(p9_SAF);
        stringBuilder.append("&").append("pa_MP=").append(pa_MP);
        stringBuilder.append("&").append("pd_FrpId=").append(pd_FrpId);
        stringBuilder.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
        stringBuilder.append("&").append("hmac=").append(hmac);

        System.out.println(stringBuilder);

        resp.sendRedirect(stringBuilder.toString());
        return null;



    }
    /**
     * 支付准备前期
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String paymentPre(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException{
        String oid = req.getParameter("oid");
        Order order = orderService.load(oid);
        req.setAttribute("order",order);
        return "f:/jsps/order/pay.jsp";
    }
    /**
     * 确认收货
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String confirm(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException{
        String oid = req.getParameter("oid");
        int status = orderService.findStatus(oid);//等到oid，校验status
        if(status !=1){
            req.setAttribute("code","error");
            req.setAttribute("msg","状态不对，不能确认收货！！");  //应该状态不对不显示取消，但可能前台伪造超链接骗出按钮
            return "f:/jsps/msg.jsp";
        }
        orderService.updateStatus(oid,5);//设置状态为5，取消
        req.setAttribute("code","success");
        req.setAttribute("msg","交易成功");
        return "f:/jsps/msg.jsp";
    }

    /**
     * 取消订单
     * @param req
     * @param resp
     * @return
     */
    public String cancel(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException{
        String oid = req.getParameter("oid");
        int status = orderService.findStatus(oid);//等到oid，校验status
        if(status !=1){
            req.setAttribute("code","error");
            req.setAttribute("msg","状态不对，不能取消！！");  //应该状态不对不显示取消，但可能前台伪造超链接骗出按钮
            return "f:/jsps/msg.jsp";
        }
        orderService.updateStatus(oid,5);//设置状态为5，取消
        req.setAttribute("code","success");
        req.setAttribute("msg","您的订单已取消");
            return "f:/jsps/msg.jsp";
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
