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
		String pid = (String) map.get("pid");
		if(pid != null){  //使用父分类对象来装载pid，然后把父分类设置给category。
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
		String sql = "select * from t_category where pid is null";  //只能是is
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
		String sql = "select * from t_category where pid = ?";
		List<Map<String,Object>>mapList = qr.query(sql, new MapListHandler(),pid);
		return toCategoryList(mapList);
	}
}
