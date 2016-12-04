package cn.itcast.goods.admin.category.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by yanke on 2016/12/2.
 */
@WebServlet(name = "AdminCategoryServlet",urlPatterns = "/admin/AdminCategoryServlet")
public class AdminCategoryServlet extends BaseServlet {

    private CategoryService categoryService = new CategoryService();
    private BookService bookService =new BookService();


    /**
     * 查询所有分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("parents", categoryService.findAll());
        return "f:/adminjsps/admin/category/list.jsp";
    }

    /**
     * 添加一级分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addParent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //封装表单数据，少了cid
        // 调用service完成添加；

        Category parent = CommonUtils.toBean(request.getParameterMap(), Category.class);

        parent.setCid(CommonUtils.uuid());//设置cid
        categoryService.add(parent);

        return findAll(request, response);
    }

    /**
     * 添加二级分类的第一步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addChildPre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pid = request.getParameter("pid");//获取当前点击的父分类
        List<Category> parents = categoryService.findParents();
        request.setAttribute("pid", pid);
        request.setAttribute("parents", parents);

        return "f:/adminjsps/admin/category/add2.jsp";
    }

    /**
     * 添加二级分类，第二步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addChild(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //封装表单数据，少了cid ,把pid映射到child对象中
        // 调用service完成添加；

        Category child = CommonUtils.toBean(request.getParameterMap(), Category.class);

        child.setCid(CommonUtils.uuid());//设置cid

        //手动映射pid
        String pid = request.getParameter("pid");
        Category parent = new Category();
        parent.setCid(pid);
        child.setParent(parent);

        categoryService.add(child);

        return findAll(request, response);
    }

    /**
     * 修改一级分类：第一步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editParentPre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        通过链接获取cid，得到Category,保存之，转发到edit.jsp显示
         */
        String cid = request.getParameter("cid");
        Category parent = categoryService.load(cid);
        request.setAttribute("parent", parent);

        return "f:/adminjsps/admin/category/edit.jsp";


    }
    /**
     * 修改一级分类：第二步.
     *父分类没有pid。
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editParent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        封装表单数据到Category，调用service完成修改，转发到list.jsp下显示；
         */
        Category parent = CommonUtils.toBean(request.getParameterMap(),Category.class);

        categoryService.edit(parent);

        return findAll(request,response);


    }

    /**
     * 加载二级分类第一步
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editChildPre(HttpServletRequest request,HttpServletResponse response)
                throws ServletException,IOException{

        String cid = request.getParameter("cid");

        Category child = categoryService.load(cid);  //加载二级分类,保存
        request.setAttribute("child",child);



        List<Category> parents =categoryService.findParents();//找到所有的一级分类,保存之
        request.setAttribute("parents",parents);


        return "f:/adminjsps/admin/category/edit2.jsp";

    }

    /**
     *加载二级分类：第二步
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editChild(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException {
        //封装表单参数到Category，封装表单中的pid，调用service中的edit完成修改

        Category child = CommonUtils.toBean(request.getParameterMap(),Category.class);

        String pid = request.getParameter("pid");

        //Category parent = categoryService.load("pid");

        Category parent = new Category();
        parent.setCid(pid);

        child.setParent(parent);

        categoryService.edit(child);

        return findAll(request,response);
    }

    /**
     * 删除一级分类
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String deleteParent(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException {
        //通过链接获取cid，通过cid查看该父分类下子分类的个数
        //大于零有二级分类，则不能删除，保存信息到msg.jsp下
        // 等于零则删除，返回list.jsp下
        String cid = request.getParameter("cid");
        int cnt = categoryService.findChildrenCountByParent(cid); //找到pid=cid的就是二级分类个数
        if(cnt > 0){
            request.setAttribute("msg","下面还有二级分类不能删除！！！");
            return "f:/adminjsps/msg.jsp";
        }else {
            categoryService.delete(cid);

            return findAll(request,response);
        }
    }

    /**
     * 删除二级分类：如果二级分类下有图书，则不能删除。BookDao
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String deleteChild(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException {
        /**
         * 页面获取cid，通过cid查询分类下是否有图书。有不能删，没有就直接删除，返回listjsp。
         */

        String cid =request.getParameter("cid");
        int cnt = bookService.findBookCountByCategory(cid);
        if(cnt > 0){
            request.setAttribute("msg","该分类下还有图书不能删除");

            return "f:/adminjsps/msg.jsp";
        }else{
            categoryService.delete(cid);

            return findAll(request,response);
        }






    }
}