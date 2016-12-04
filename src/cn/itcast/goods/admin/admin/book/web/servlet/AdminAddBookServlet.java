package cn.itcast.goods.admin.admin.book.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/4.
 */
@WebServlet(name = "AdminAddBookServlet",urlPatterns = "/admin/AdminAddBookServlet")
public class AdminAddBookServlet extends HttpServlet {
    //不能继承BaseServlet，因为这个servlet要有上传表单，
    //上传时必须为do Post,BaseServlet中的getparameter()在上传时不能使用
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
