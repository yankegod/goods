package cn.itcast.goods.admin.order.web.servlet;

import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/5.
 */
@WebServlet(name = "AdminOrderServlet",urlPatterns = "/admin/AdminOrderServlet")
public class AdminOrderServlet extends BaseServlet {
    private OrderService orderService = new OrderService();
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
     * 查询所有订单
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int pc =getPc(req);
        String url = getUrl(req);


        PageBean<Order> pageBean = orderService.findAll(pc);

        pageBean.setUrl(url);
        req.setAttribute("pb",pageBean);

        return "f:/adminjsps/admin/order/list.jsp";
    }

    /**
     * 按状态查询
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int pc =getPc(req);
        String url = getUrl(req);

        int status = Integer.parseInt(req.getParameter("status"));


        PageBean<Order> pageBean = orderService.findByStatus(status,pc);

        pageBean.setUrl(url);
        req.setAttribute("pb",pageBean);

        return "f:/adminjsps/admin/order/list.jsp";
    }

    /**
     * 查看订单详细信息
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
        String btn =  req.getParameter("btn");//确定用户用哪个超链接来访问本方法的。
        req.setAttribute("btn",btn);
        return "f:/adminjsps/admin/order/desc.jsp";
    }

    /**
     * 取消订单
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String cancel(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException{
        String oid = req.getParameter("oid");
        int status = orderService.findStatus(oid);//等到oid，校验status
        if(status !=1){
            req.setAttribute("code","error");
            req.setAttribute("msg","状态不对，不能取消！！");  //应该状态不对不显示取消，但可能前台伪造超链接骗出按钮
            return "f:/adminjsps/msg.jsp";
        }
        orderService.updateStatus(oid,5);//设置状态为5，取消
        req.setAttribute("code","success");
        req.setAttribute("msg","您的订单已取消");
        return "f:/adminjsps/msg.jsp";
    }

    /**
     * 发货，修改状态
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String deliver(HttpServletRequest req ,HttpServletResponse resp)throws ServletException,IOException{
        String oid = req.getParameter("oid");
        int status = orderService.findStatus(oid);//等到oid，校验status
        if(status !=2){
            req.setAttribute("code","error");
            req.setAttribute("msg","状态不对，不能发货！！");  //应该状态不对不显示取消，但可能前台伪造超链接骗出按钮
            return "f:/adminjsps/msg.jsp";
        }
        orderService.updateStatus(oid,3);//设置状态为3.
        req.setAttribute("code","success");
        req.setAttribute("msg","您的订单已发货！！！！！！！！！");
        return "f:/adminjsps/msg.jsp";
    }

}
