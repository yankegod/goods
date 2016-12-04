package cn.itcast.goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.page.Expression;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.page.PageConstants;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	QueryRunner qr = new TxQueryRunner();

	/**
	 * 通过bid查询
	 * @param bid
	 * @return
	 */
	public Book findByBid(String bid) throws SQLException {
		String sql = "select * from t_book where bid = ?";
		//map中有图书信息 和 cid得到一个Category
		Map<String,Object> map = qr.query(sql,new MapHandler(),bid);

		Book book = CommonUtils.toBean(map,Book.class);

		Category category = CommonUtils.toBean(map,Category.class);

		book.setCategory(category);

		return book;
	}
	
	/**
	 * 按分类查询
	 * @param cid
	 * @param pc
	 * @throws SQLException 
	 * @retur
	 */
	public PageBean<Book> findByCategory(String cid,int pc) throws SQLException{
		List<Expression> exprList =new ArrayList<Expression>();
		exprList.add(new Expression("cid","=",cid));
		return findByCriteria(exprList, pc);
		
	}
	/**
	 * 按书名模糊查询
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname,int pc) throws SQLException{
		List<Expression> exprList =new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+bname+"%"));
		return findByCriteria(exprList, pc);
	}
	/**
	 * 按作者查
	 * @param author
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author,int pc) throws SQLException{
		List<Expression> exprList =new ArrayList<Expression>();
		exprList.add(new Expression("author","like","%"+author+"%"));
		return findByCriteria(exprList, pc);
	}
	/**
	 * 按出版社查
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press,int pc) throws SQLException{
		List<Expression> exprList =new ArrayList<Expression>();
		exprList.add(new Expression("press","like","%"+press+"%"));
		PageBean<Book> pb = findByCriteria(exprList, pc);
		System.out.println(press+"BookDao"+pb);
		return findByCriteria(exprList, pc);
	}
	/**
	 * 多条件组合查询
	 * @param criteria
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria,int pc) throws SQLException{
		List<Expression> exprList =new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+criteria.getBname()+"%"));
		exprList.add(new Expression("author","like","%"+criteria.getAuthor()+"%"));
		exprList.add(new Expression("press","like","%"+criteria.getPress()+"%"));
		PageBean<Book> pb = findByCriteria(exprList, pc);
		System.out.println(pb);
		return pb;
	}
	/**
	 * 通用的查询方法得到ps\tr\beanList创建PageBean
	 * @return
	 * @throws SQLException 
	 */
	private PageBean<Book> findByCriteria(List<Expression> exprList,int pc) throws SQLException{

		int ps = PageConstants.BOOK_PAGE_SIZE; //每页记录数
		
		//生成where子句
		StringBuilder whereSql  = new StringBuilder("where 1=1");
		
		List<Object> params = new ArrayList<Object>(); //sql中的“？”
		
		for(Expression expr : exprList){
			/*
			 * 添加一个以and开头 ，条件的名称，条件运算符 ， is null没有值。
			 * */
			whereSql.append(" and ").append(expr.getName()).append(" ")
			.append(expr.getOperator()).append(" ");
			//where 1=1 and bid = 
			if(!expr.getOperator().equals("is null")){
				whereSql.append("?");
				params.add(expr.getValue());
			}
				
		}
		// System.out.println(whereSql); //where 1=1 and bid = ? and bname like ? and edition is null 
		 //System.out.println(params); // [1, %java%]

		
		String sql = "select count(*) from t_book "+whereSql;
		
		Number number  = (Number)qr.query(sql, new ScalarHandler(),params.toArray());
		int tr = number.intValue();    //总记录数！！！
		
		//得到当前记录，beanList
		sql = "select * from t_book " + whereSql+" order by orderBy limit ?,?";
		
		params.add((pc-1)*ps);   //当前页首行记录的下标
		params.add(ps);   //一共查询几行
		
		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(Book.class),
				params.toArray());
        /*
         * 创建pageBean，设置参数，其中无url由servlet提供
         */
		PageBean<Book> pb = new PageBean<>();
		pb.setBeanList(beanList);
		pb.setPs(ps);
		pb.setPc(pc);;
		pb.setTr(tr);
		
		return pb;
	}

	/**
	 * 通过分类查询图书数量
	 * @param cid
	 * @return
	 */
	public int findBookCountByCategory(String cid) throws SQLException {
		String sql = "select count(*) from t_book where cid = ?";

		Number cnt = (Number)qr.query(sql,new ScalarHandler(), cid);

		return cnt == null ? 0 :cnt.intValue();
	}


	//	public static void main(String[] args) throws SQLException {
//		BookDao bookDao = new BookDao();
//		List<Expression> exprList =new ArrayList<>();
//		exprList.add(new Expression("bid","=","1"));
//   	exprList.add(new Expression("bname","like","%java%"));
//		exprList.add(new Expression("edition","is null",null));
//		
//		//bookDao.findByCriteria(exprList, 10);
//	}

}
