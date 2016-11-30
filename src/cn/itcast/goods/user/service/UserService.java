package cn.itcast.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

/**
 * 用户模块业务层
 * @author qdmmy6
 *
 */
/**
 * 登录功能
 * @author yanke
 *
 */
public class UserService {
	private UserDao userDao = new UserDao();
	/**
	 * 修改密码：校验老密码,修改新密码！！
	 * @throws UserException 
	 */
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException{
		try {
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool){
				throw new UserException("老密码错误");
			}
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);	
		}
	}
	/**
	 * 登录功能
	 * @param user
	 * @return
	 */
	public User login(User user){
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 激活功能：查询用户、为空则无效激活码抛出异常、查看用户状态是否为true。
	 * @param code
	 * @throws UserException 
	 */
	public void activation(String code) throws UserException{
		
		try {
			User user = userDao.findByCode(code);
			if(user == null)throw new UserException("无效激活码！！");
			if(user.isStatus() == true)throw new UserException("您已经激活了！！");
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * ajax登录名验证
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);      //转换异常！！
		}
	}
	public boolean ajaxValidateEmail(String email) {
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);      //转换异常！！
		}
	}
	/**
	 * 注册功能
	 * 补齐数据 插入数据库 发邮件
	 * @param user
	 */
	public void regist(User user){
		
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);   //转换异常
		}
		
		/*登录邮件服务器，得到session*/
		Properties properties =new Properties();  //通过配置文件获取
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {    //本类的类加载器，把配置文件内容加载到properties中
			throw new RuntimeException(e1);
		}
		
		String host=properties.getProperty("host");     //服务器主机名；
		String name =properties.getProperty("username");    //登录用户名；
		String pass=properties.getProperty("password");
		Session  session = MailUtils.createSession(host, name, pass);
		
		//MessageFormat.format把第一个参数中的{0}，用第二个参数来替换。
		//example：MessageFormat.format("你好{0}，你{1}","张三","去屎吧")。
		String from=properties.getProperty("from");
		String to=user.getEmail();
		String subject=properties.getProperty("subject");
		String content=MessageFormat.format(properties.getProperty("content"), user.getActivationCode());
		
		Mail mail = new Mail(from,to,subject,content);
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
