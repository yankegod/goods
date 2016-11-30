package cn.itcast.goods.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.servlet.BaseServlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BookServlet")
public class BookServlet extends BaseServlet {

	BookService bookService = new BookService();
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
	 * 按bid查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book",book);

		return "f:/jsps/book/desc.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		 url=URLDecoder.decode(url, "UTF-8");
	  //获取查询条件
		
		String press = URLDecoder.decode(req.getParameter("press"),"UTF-8");
		
	  //用cid 和pc 得到PageBean
		PageBean<Book> pb = bookService.findByPress(press, pc);
	  //给PageBean设置url，保存，转发到/jsps/book/list.jsp
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		System.out.println(pb);
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
	}
}
