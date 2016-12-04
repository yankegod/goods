package cn.itcast.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 * 分类持久层
 * @author yanke
 *
 */
public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 把一个map映射到category中去
	 * map{cid:XX,cname:xx,pid:xx,desc:xx,orderBy：xx}
	 * Category{cid:xx,cname:xx,parent:xx (cid=pid),desc:xx}
	 */
	private Category toCategory(Map<String,Object> map){
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");  //一级分类pid为null；
		if(pid != null){                //使用父分类对象来装载pid，然后把父分类设置给category。
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	/*
	 * 
	 * 把多个Map（List<Map>）映射成多个category(List<Category>)
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map: mapList){
			Category c =toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	/**
	 * 返回所有分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findAll() throws SQLException{
		/*
		 * 查询所有一级分类
		 */
		String sql = "select * from t_category where pid is null order by orderBy";  //只能是is
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler());
		
		List<Category> parents =toCategoryList(mapList);
		
		for(Category parent :parents){ //查询父分类下所有子分类，设置给父类
			List<Category> children = findByParent(parent.getCid());
			parent.setChildren(children);
		}
		return parents;
	}
	/**
	 * 通过父类查询子分类
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException{
		String sql = "select * from t_category where pid = ? order by orderBy";
		List<Map<String,Object>>mapList = qr.query(sql, new MapListHandler(),pid);
		return toCategoryList(mapList);
	}

	/**
	 * 添加一级分类，或二级分类；
	 * @param category
	 */
	public void add(Category category) throws SQLException {  //desc是关键字要加’‘;

		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";

		//一级分类没有parent；
		String pid = null;
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}

		Object [] params = {category.getCid(),category.getCname(),pid,category.getDesc()};

		qr.update(sql,params);

	}

	/**
	 * 找到所有一级分类
	 * @return
	 */
	public List<Category >findParents() throws SQLException {

		String sql = "select * from t_category where pid is null order by orderBy;";

		List<Map<String,Object>> mapList = qr.query(sql,new MapListHandler());

		return  toCategoryList(mapList);
	}

	/**
	 * 加载分类：一级分类和二级分类
	 * @param cid
	 * @return
	 */
	public  Category load(String cid) throws SQLException {

		String sql = "select * from t_category where cid = ?;";

		return toCategory(qr.query(sql,new MapHandler(),cid));
		                  //toCategory方法以处理一级二级分类关系

	}

	/**
	 * 修改分类：一级分类和二级分类
	 * @param category
	 */
	public void edit(Category category) throws SQLException {

		String sql = "update t_category set cname = ?,pid = ?,`desc`=? where cid = ?";

		String pid = null;

		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}

		Object[] params = {category.getCname(),pid,category.getDesc(),category.getCid()};

		qr.update(sql,params);  //注意对pid为空的处理。

	}

	/**
	 * 查询指定父分类下子分类的个数
	 * @param pid
	 * @return
	 */
	public int findChildrenCountByParent(String pid) throws SQLException {

		String sql ="select count(*) from t_category where pid =? ;";

		Number cnt = (Number) qr.query(sql, new ScalarHandler(),pid);

		return  cnt == null ? 0:cnt.intValue();  //在他为空时返回0，避免出现空指针异常。
	}

	/**
	 * 删除分类
	 * @param cid
	 */
	public void delete(String cid) throws SQLException {

		String sql ="delete from t_category where cid =?";
		qr.update(sql,cid);

	}
}
