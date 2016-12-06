package cn.itcast.goods.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Created by yanke on 2016/12/4.
 */
@WebServlet(name = "AdminBookServlet",urlPatterns = "/admin/AdminBookServlet")
public class AdminBookServlet extends BaseServlet {
    BookService bookService =new BookService();
    CategoryService categoryService =new CategoryService();

    /**
     * 删除图书
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String delete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String bid = req.getParameter("bid");

        Book book =bookService.load(bid);

        //删除途径
        String savepath = this.getServletContext().getRealPath("/");  //获取真实路径。

        File file = new File(savepath,book.getImage_w());
        file.delete();
System.out.println(file.getAbsolutePath());
        new File(savepath,book.getImage_b()).delete();
        bookService.delete(bid);//删除数据库数据;
        req.setAttribute("msg","删除图书成功！！！");

        return "f:/adminjsps/msg.jsp";
    }
    /**
     * 显示所有分类
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Category> parents= categoryService.findAll();
        req.setAttribute("parents", parents);
        return "f:/adminjsps/admin/book/left.jsp";
    }

    /**
     * 获取当前页码
     * @param req
     * @return
     */
    private int getPc(HttpServletRequest req){
        int pc = 1;
        String params = req.getParameter("pc");
        if(params != null && !params.trim().isEmpty()){
            try{
                pc = Integer.parseInt(params);  //如果传个abc那么进入catch块啥都不干，默认为1.
            }catch(RuntimeException e){

            }
        }
        return pc;
    }
    /**
     * uri 抽象定义     url肯定是uri更为具体 绝对
     * 截取url，分页导航作为超链接导航。
     * @param req
     * @return
     */
    private String getUrl(HttpServletRequest req){
		/*
		 * http://localhost:8080/goods/BookServlet?method=findByCategory&cid=xxx&pc=xxx
		 * 如果参数中存在pc，接去掉
		 */
        String url = req.getRequestURI()+"?"+req.getQueryString();

        int index = url.lastIndexOf("&pc=");
        if(index != -1){
            url = url.substring(0,index);
        }
        return url;

    }


    /**
     * 按分类查询
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //1.得到pc :页面有使用页面的，没有就为1
        int pc = getPc(req);
        //2.得到url
        String url = getUrl(req);
        //获取查询条件
        String cid =req.getParameter("cid");
        //用cid 和pc 得到PageBean
        PageBean<Book> pb = bookService.findByCategory(cid, pc);
        //给PageBean设置url，保存，转发到/jsps/book/list.jsp
        pb.setUrl(url);
        req.setAttribute("pb", pb);
        return "f:/adminjsps/admin/book/list.jsp";
    }
    /**
     * 按作者查
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //1.得到pc :页面有使用页面的，没有就为1
        int pc = getPc(req);
        //2.得到url
        String url = getUrl(req);
        //获取查询条件
        String author =req.getParameter("author");
        //用cid 和pc 得到PageBean
        PageBean<Book> pb = bookService.findByAuthor(author, pc);
        //给PageBean设置url，保存，转发到/jsps/book/list.jsp
        pb.setUrl(url);
        req.setAttribute("pb", pb);
        return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 按出版社查询
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByPress(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //1.得到pc :页面有使用页面的，没有就为1
        int pc = getPc(req);
        //2.得到url
        String url = getUrl(req);
//		try {
//			url= URLDecoder.decode(url,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        url= URLDecoder.decode(url, "UTF-8");
        //获取查询条件

        String press = URLDecoder.decode(req.getParameter("press"),"UTF-8");

        //用cid 和pc 得到PageBean
        PageBean<Book> pb = bookService.findByPress(press, pc);
        //给PageBean设置url，保存，转发到/jsps/book/list.jsp
        pb.setUrl(url);
        req.setAttribute("pb", pb);
// System.out.println(pb);
        return "f:/adminjsps/admin/book/list.jsp";
    }
    /**
     * 按书名差
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByBname(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //1.得到pc :页面有使用页面的，没有就为1
        int pc = getPc(req);
        //2.得到url
        String url = getUrl(req);
        //获取查询条件
        String bname =req.getParameter("bname");
        //用cid 和pc 得到PageBean
        PageBean<Book> pb = bookService.findByBname(bname, pc);
        //给PageBean设置url，保存，转发到/jsps/book/list.jsp
        pb.setUrl(url);
        req.setAttribute("pb", pb);
        System.out.println(pb);
        return "f:/adminjsps/admin/book/list.jsp";
    }
    /**
     * 组合查询 :得到Book对象
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //1.得到pc :页面有使用页面的，没有就为1
        int pc = getPc(req);
        //2.得到url
        String url = getUrl(req);
        //获取查询条件
        Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
        //用cid 和pc 得到PageBean
        PageBean<Book> pb = bookService.findByCombination(criteria, pc);
        //给PageBean设置url，保存，转发到/jsps/book/list.jsp
        pb.setUrl(url);
        req.setAttribute("pb", pb);
        return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 添加图书第一步
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addPre(HttpServletRequest request,HttpServletResponse response)
                throws ServletException,IOException{
        /*
        获取所有一级分类，保存之，转发到add.jsp
         */
        List<Category> parents = categoryService.findParents();
        request.setAttribute("parents",parents);

        return "f:/adminjsps/admin/book/add.jsp";

    }

    /**
     * 加载指定父分类下的二级分类
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String ajaxFindChildren(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException{

        String pid = request.getParameter("pid");

        List<Category> children =categoryService.findByparent(pid);
        //把List<Category>转换成json
        String json =toJson(children);
   System.out.println(json);
        response.getWriter().print(json);
        return null;
    }

    private String toJson(Category category){
        StringBuilder stringBuilder =new StringBuilder("{");
        stringBuilder.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
        stringBuilder.append(",");
        stringBuilder.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    //[{"cid":"dfsdf",},{}]
    private String toJson(List<Category> categoryList){
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i=0 ;i<categoryList.size();i++){
            stringBuilder.append(toJson(categoryList.get(i)));

            if(i < categoryList.size()-1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    /**
     * 加载图书
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String load(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException {
        String bid = request.getParameter("bid");
        Book book = bookService.load(bid);   //获取bid，得到book对象。
        request.setAttribute("book",book);

        //获取所有一级分类
        List<Category> parents = categoryService.findParents();
        request.setAttribute("parents",parents);

        //获取当前图书所属的一级分类的所有二级分类

        List<Category> chidren = categoryService.findByparent(book.getCategory().getParent().getCid());
        request.setAttribute("children",chidren);

        return "f:/adminjsps/admin/book/desc.jsp";
    }
    public String edit(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException {
        //把表单数据封装到book中，把cid封装到Categoy中，
        Map map = request.getParameterMap();
        Book book = CommonUtils.toBean(map,Book.class);
        Category category =CommonUtils.toBean(map,Category.class);
       // Category category  = new Category();
        // category.setCid(request.getParameter("cid"));
        book.setCategory(category);
        bookService.edit(book);
        request.setAttribute("msg","修改图书成功");

        return "f:/adminjsps/msg.jsp";
    }
}
