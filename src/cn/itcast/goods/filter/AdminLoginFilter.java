package cn.itcast.goods.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/6.
 */
@WebFilter(filterName = "AdminLoginFilter",urlPatterns = {"/adminjsps/admin/*","/admin/*"} )
public class AdminLoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request =(HttpServletRequest)req;

        Object admin = request.getSession().getAttribute("admin");
        if(admin == null){
            request.setAttribute("msg","您还没有登录不要瞎溜达！！！");

            request.getRequestDispatcher("/adminjsps/login.jsp").forward(request,resp);

        }else {
            chain.doFilter(req, resp);

        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
