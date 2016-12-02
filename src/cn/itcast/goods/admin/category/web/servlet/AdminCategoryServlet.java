package cn.itcast.goods.admin.category.web.servlet;

import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanke on 2016/12/2.
 */
@WebServlet(name = "AdminCategoryServlet",urlPatterns = "/admin/AdminCategoryServlet")
public class AdminCategoryServlet extends BaseServlet {

    private CategoryService categoryService =new CategoryService();

    /**
     * 查询所有分类
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            request.setAttribute("parents",categoryService.findAll());
            return "f:/adminjsps/admin/category/list.jsp";
    }

}
