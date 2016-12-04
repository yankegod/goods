package cn.itcast.goods.filter;

import cn.itcast.goods.user.domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/2.
 */
@WebFilter(filterName = "LoginFilter",urlPatterns={"/jsps/cart/*","/CartItemServlet","/jsps/order/*","/OrderServlet"})
public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;

        Object user = request.getSession().getAttribute("sessionUser");

      // User user = (User) ((HttpServletRequest)req).getSession().getAttribute("userSession");
       if(user == null){
           req.setAttribute("code","error");
           req.setAttribute("msg","您还没有登录请先登录！！");
           req.getRequestDispatcher("/jsps/msg.jsp").forward(req,resp);
       }else{
           chain.doFilter(req, resp);  //放行
       }



    }

    public void init(FilterConfig config) throws ServletException {

    }

}
