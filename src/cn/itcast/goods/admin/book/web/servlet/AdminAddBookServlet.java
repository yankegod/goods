package cn.itcast.goods.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanke on 2016/12/4.
 */
@WebServlet(name = "AdminAddBookServlet",urlPatterns = "/admin/AdminAddBookServlet")
public class AdminAddBookServlet extends HttpServlet {
    BookService bookService = new BookService();
    //不能继承BaseServlet，因为这个servlet要有上传表单，
    //上传时必须为do Post,BaseServlet中的getparameter()在上传时不能使用
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

		/*
		 * 1. commons-fileupload的上传三步
		 */
        // 创建工具
        FileItemFactory factory = new DiskFileItemFactory();
		/*
		 * 2. 创建解析器对象
		 */
        ServletFileUpload sfu = new ServletFileUpload(factory);
        sfu.setFileSizeMax(80 * 1024);//设置单个上传的文件上限为80KB
		/*
		 * 3. 解析request得到List<FileItem>
		 */
        List<FileItem> fileItemList = null;
        try {
            fileItemList = sfu.parseRequest(request);
        } catch (FileUploadException e) {
            // 如果出现这个异步，说明单个文件超出了80KB
            error("上传的文件超出了80KB", request, response);
            return;
        }

		/*
		 * 4. 把List<FileItem>封装到Book对象中
		 * 4.1 首先把“普通表单字段”放到一个Map中，再把Map转换成Book和Category对象，再建立两者的关系
		 */
        Map<String,Object> map = new HashMap<String,Object>();
        for(FileItem fileItem : fileItemList) {
            if(fileItem.isFormField()) {//如果是普通表单字段
                map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
            }
        }
        Book book = CommonUtils.toBean(map, Book.class);//把Map中大部分数据封装到Book对象中
        Category category = CommonUtils.toBean(map, Category.class);//把Map中cid封装到Category中
        book.setCategory(category);

       // System.out.println(book);

        book.setBid(CommonUtils.uuid());   //添加以个bid。
        //把上传图片保存起来：
        // 获取文件名截取之，给文件添加前缀，uuid。
        // 校验文件扩展名。
        //图片的校验尺寸大小。
        //指定图片的保存路径ServletContext#getRealPath(),保存之，设置给book对象
        FileItem fileItem1 = fileItemList.get(1);   //获取大图所在的输入框，第二个，下标为1.
        String filename = fileItem1.getName();
        int index = filename.lastIndexOf("\\");
        if(index != -1){
            filename = filename.substring(index+1);
        }
        filename = CommonUtils.uuid()+"_"+filename;   //避免文件重名

        if(!filename.toLowerCase().endsWith(".jpg")){
            error("上传图片扩展名必须是jpg",request,response);
            return;   //千万不能少！！！
        }

        //校验图片尺寸：保存上传图片，图片new成图片对象 Image ，Icon，ImageIcon，BufferedImage，ImageIO
        String savepath = this.getServletContext().getRealPath("/book_img");//保存真实路径

        File destFile = new File(savepath,filename);  //创建目标文件
        try {
            fileItem1.write(destFile);   //把临时文件重定向到指定的路径，再删除临时文件。二次调用会出现找不到文件的情况。
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //使用文件路径创建ImageIcon，得到Image对象，获取宽高
        ImageIcon icon = new ImageIcon(destFile.getAbsolutePath());
        Image image = icon.getImage();

        if(image.getWidth(null)>350||image.getHeight(null)>350){
            error("您上传的文件尺寸超过了350*350",request,response);
            destFile.delete();  //删除文件。
            return;
        }
        //把图片的路径设置给book对象
        book.setImage_w("book_img/"+filename);




        //得到小图
         fileItem1 = fileItemList.get(2);   //获取大图所在的输入框，第二个，下标为1.
         filename = fileItem1.getName();
         index = filename.lastIndexOf("\\");
        if(index != -1){
            filename = filename.substring(index+1);
        }
        filename = CommonUtils.uuid()+"_"+filename;   //避免文件重名

        if(!filename.toLowerCase().endsWith(".jpg")){
            error("上传图片扩展名必须是jpg",request,response);
            return;   //千万不能少！！！
        }

        //校验图片尺寸：保存上传图片，图片new成图片对象 Image ，Icon，ImageIcon，BufferedImage，ImageIO
         savepath = this.getServletContext().getRealPath("/book_img");//保存真实路径
 System.out.println("==================="+savepath);
        String savepath1 = this.getServletContext().getContextPath() ;
 System.out.println("==================="+savepath1);
         destFile = new File(savepath,filename);  //创建目标文件
        try {
            fileItem1.write(destFile);   //把临时文件重定向到指定的路径，再删除临时文件。二次调用会出现找不到文件的情况。
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //使用文件路径创建ImageIcon，得到Image对象，获取宽高
         icon = new ImageIcon(destFile.getAbsolutePath());
         image = icon.getImage();

        if(image.getWidth(null)>350||image.getHeight(null)>350){
            error("您上传的文件尺寸超过了350*350",request,response);
            destFile.delete();  //删除文件。
            return;
        }
        //把图片的路径设置给book对象
        book.setImage_b("book_img/"+filename);
//System.out.println(book);
        //调用service保存
        bookService.add(book);
        //保存成功信息
        request.setAttribute("msg","添加图书成功");
        request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request,response);
    }

    /**
     * 保存错误信息转发到add.jsp
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void error(String msg,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("msg",msg);
        request.setAttribute("parents",new CategoryService().findParents()); //回显时带回所有一级分类。

        request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request,response);
    }
}
