package cn.itcast.goods.category.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.goods.category.dao.CategoryDao;
import cn.itcast.goods.category.domain.Category;

public class CategoryService {
	CategoryDao categoryDao = new CategoryDao();

	/**
	 * 查询所有
	 * @return
	 */
	public List<Category> findAll(){
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 获取所有父分类，不带子分类
	 * @return
	 */
	public List<Category> findParents(){
		try {
			return categoryDao.findParents();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category){
		try {
			categoryDao.add(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 编辑分类
	 * @param category
	 */
	public void edit(Category category){
		try {
			categoryDao.edit(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
	/**
	 * 加载分类
	 * @param cid
	 * @return
	 */
	public Category load(String cid){
		try {
			return categoryDao.load(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询子分类的数目
	 * @param pid
	 * @return
	 */
	public int findChildrenCountByParent(String pid){
		try {
			return categoryDao.findChildrenCountByParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除分类
	 * @param cid
	 */
	public void delete(String cid){
		try {
			categoryDao.delete(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
