package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;

/**
 * 用户模块WEB层
 * @author qdmmy6
 *
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {

	private UserService userService = new UserService();
	
	/**
	 * 退出功能: req.getSession().invalidate();
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
	/**
	 * 修改密码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		
		User user =(User)req.getSession().getAttribute("sessionUser");  //在login方法里将User放入到session中名为sessionUser。
		if(user == null){
			req.setAttribute("msg", "您还没有登陆！");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getNewpass(), 
					formUser.getLoginpass());
			req.setAttribute("msg", "修改成功！！！");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";    
			
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());   //保存异常信息到request
			req.setAttribute("user", formUser);        //为了回显信息！！！
			
			return "f:/jsps/user/pwd.jsp";
		}
	}	
	/**
	 * ajax用户名是否注册
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidataLoginname(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String loginname = req.getParameter("loginname");  //获取用户名
		boolean bool = userService.ajaxValidateLoginname(loginname); //调用service方法进行验证
		resp.getWriter().print(bool);  //发给客户端
		return null;     //不转发也不重定向
	}
	/**
	 * ajax邮箱是否正确
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException ajaxValidataEmail
	 * @throws IOException
	 */
	public String ajaxValidataEmail(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String email = req.getParameter("email");
		boolean bool = userService.ajaxValidateEmail(email);
		resp.getWriter().print(bool);
		return null;
	}
	/**
	 * 
	 * 验证码校验
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp) 
throws ServletException, IOException {
		/*
		 * 获取输入框验证码
		 * 获取真实验证码
		 * 进行比较忽略大小写，得到结果
		 * 发送给客户端
		 * */
		String verifyCode = req.getParameter("verifyCode");
		
		String vcode = (String)req.getSession().getAttribute("vCode");  //需要强转
		
		boolean b = vcode.equalsIgnoreCase(verifyCode);     //
		
		resp.getWriter().print(b);       //发送给客户端；ajax接受
		return null;
	}
	/**
	 * 封装表单数据到javaBean
	 * 
	 * 校验参数:失败 保存错误信息还给regist.jsp。  Map UserServlet#validateREgist(){}
	 * 
	 * 把表单数据给service完成业务
	 * 保存成功信息msg.jsp
	 * 转发到写字板显示成功信息
	 * */
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2. 校验之, 如果校验失败，保存错误信息，返回到regist.jsp显示
		 */
		Map<String,String> errors = validateRegist(formUser, req.getSession());
		if(errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. 使用service完成业务
		 */
		userService.regist(formUser);
		/*
		 * 4. 保存成功信息，转发到msg.jsp显示！
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册功能，请马上到邮箱激活！");
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * 注册校验
	 * 对表单的字段进行逐个校验，如果有错误，使用当前字段名称为key，错误信息为value，保存到map中
	 * 返回map
	 */
	private Map<String,String> validateRegist(User formUser, HttpSession session) {
		Map<String,String> errors = new HashMap<String,String>();
		/*
		 * 1. 校验登录名
		 */
		String loginname = formUser.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else if(!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册！");
		}
		
		/*
		 * 2. 校验登录密码
		 */
		String loginpass = formUser.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		/*
		 * 3. 确认密码校验
		 */
		String reloginpass = formUser.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if(!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "两次输入不一致！");
		}
		
		/*
		 * 4. 校验email
		 */
		String email = formUser.getEmail();
		if(email == null || email.trim().isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "Email格式错误！");
		} else if(!userService.ajaxValidateEmail(email)) {
			errors.put("email", "Email已被注册！");
		}
		
		/*
		 * 5. 验证码校验
		 */
		String verifyCode = formUser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if(!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误！");
		}
		
		return errors;
	}
	/**
	 * 获取激活码，调用service完成激活，service异常或成功信息则保存到request中转发到msg.jsp显示！！
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String code = req.getParameter("activationCode");
		req.setAttribute("code", "success");
		req.setAttribute("msg", "恭喜，激活成功");
		try {
			userService.activation(code);
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code","error");   //通知msg.jsp显示X
		}
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 封装表单到User、校验表单数据。用service查询。用户不存在保存错误信息，
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp) 
throws ServletException, IOException {
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		
		Map<String,String> errors = validateLogin(formUser, req.getSession());
		if(errors.size()>0){
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			System.out.println("有错误");
			return "f:/jsps/user/login.jsp";
		}
		User user = userService.login(formUser);
		
		if(user == null){
			req.setAttribute("msg", "用户名或密码错误");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		}else{
			if(!user.isStatus()){
				req.setAttribute("msg", "您还没激活!!");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			}else{     //
				req.getSession().setAttribute("sessionUser", user);
				
				String loginname = user.getLoginname();
                loginname = URLEncoder.encode(loginname,"utf-8");   //设置编码格式！！！
				Cookie cookie = new Cookie("loginname", loginname);  //设置时间
				cookie.setMaxAge(60*60*24*10);
				resp.addCookie(cookie);
				
				return "r:/index.jsp";
			}
		}
	}

/**
 * 登录验证
 * @param formUser
 * @param session
 * @return
 */
private Map<String,String> validateLogin(User formUser, HttpSession session) {
	Map<String,String> errors = new HashMap<String,String>();
	
	//校验用户名
	String loginname = formUser.getLoginname();
	if(loginname == null || loginname.trim().isEmpty()){
		errors.put("loginname", "用户名不能为空！！！");
		System.out.println("用户名不能为空！！！");
	}else if(loginname.length()<3 || loginname.length()>8){
		errors.put("loginname", "用户名长度3-8之间");
		System.out.println("用户名长度3-8之间");
	}
	
	//后台密码校验
	String loginpass = formUser.getLoginpass();
	if(loginpass == null || loginpass.trim().isEmpty()){
		errors.put("loginpass", "密码不能为空");
		System.out.println("密码不能为空");
	}else if(loginpass.length()<3 || loginpass.length()>8){
		errors.put("loginpass", "密码长度3-8之间");
		System.out.println("密码长度3-8之间");
	}
	
	//后台验证码校验
	String verifyCode = formUser.getVerifyCode();
	String vcode = (String) session.getAttribute("vCode");
	if(verifyCode == null || verifyCode.trim().isEmpty()) {
		errors.put("verifyCode", "验证码不能为空！");
		System.out.println("验证码不能为空！");
	}else if(!verifyCode.equalsIgnoreCase(vcode)){
		errors.put("verifyCode", "验证码不正确！！");
		System.out.println("验证码不正确！！");
	}
	return errors;
}
}