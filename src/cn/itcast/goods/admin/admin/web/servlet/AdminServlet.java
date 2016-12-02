package cn.itcast.goods.admin.admin.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/2.
 */
@WebServlet(name = "AdminServlet" ,urlPatterns = "/AdminServlet")
public class AdminServlet extends BaseServlet {

    private AdminService adminService =new AdminService();

    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Admin form = CommonUtils.toBean(request.getParameterMap(),Admin.class);
        Admin admin = adminService.login(form);

        if (admin == null){
            request.setAttribute("msg","用户名或密码错误");
            return "f:/adminjsps/msg.jsp";
        }else {
            request.getSession().setAttribute("admin",admin);  //把用户放到session里面
            return  "r:/adminjsps/admin/index.jsp";
        }
    }

}
