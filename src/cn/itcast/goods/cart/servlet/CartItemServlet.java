package cn.itcast.goods.cart.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yanke on 2016/11/29.
 */
@WebServlet("/CartItemServlet")
public class CartItemServlet extends BaseServlet {
    private CartItemService cartItemService = new CartItemService();
    /**
     * 加载多个cartItem
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //获取cartItemIds参数
        String cartItemIds = req.getParameter("cartItemIds");
        //通过service的到List<CartItem>
        List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);

        //把上个页面的total传到下一个页面
        double total = Double.parseDouble(req.getParameter("total"));//后台获取的字符串要转换格式
        req.setAttribute("total",total);
        //保存到req中
        req.setAttribute("cartItemList",cartItemList);
        System.out.println(cartItemList);
        req.setAttribute("cartItemIds",cartItemIds);  //继续传到下一个页面，用于结账时选择
        //转发到/cart/showitems.jsp
        return "f:/jsps/cart/showitem.jsp";
    }


    /**
     * 修改条目：要修改谁，要改成什么
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String cartItemId = req.getParameter("cartItemId");
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        CartItem cartItem = cartItemService.updateQuantity(cartItemId,quantity);

        StringBuilder stringBuilder = new StringBuilder("{");//ajax调用，需返回json对象
        stringBuilder.append("\"quantity\"").append(":").append(cartItem.getQuantity());
        stringBuilder.append(",");
        stringBuilder.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
        stringBuilder.append("}");

        System.out.println(stringBuilder);

        resp.getWriter().print(stringBuilder);   //给客户端返回了一个json对象，js自动解析。

        return null;
    }
    /**
     * 批量删除
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String batchDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            String cartItemIds =req.getParameter("cartItemIds");
            cartItemService.batchDelete(cartItemIds);
            return myCart(req,resp);
    }
    /**
     * 添加购物车条目
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public String add(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //封装表单数据 bid和quantity
        Map map =req.getParameterMap();
        CartItem cartItem = CommonUtils.toBean(map,CartItem.class);
        Book book = CommonUtils.toBean(map,Book.class);
        User user = (User) req.getSession().getAttribute("sessionUser");
        cartItem.setBook(book);
        cartItem.setUser(user);

        //调用service完成添加。
        cartItemService.add(cartItem);

        //查询所有条目转发到list.jsp显示
        return myCart(req,resp);


    }

    /**
     * 我的购物车
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String  myCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //等到uid
        User user = (User)req.getSession().getAttribute("sessionUser");
        String uid = user.getUid();

       List<CartItem> cartItemList = cartItemService.myCart(uid);

       req.setAttribute("cartItemList",cartItemList);

       return "f:/jsps/cart/list.jsp";
    }
}
