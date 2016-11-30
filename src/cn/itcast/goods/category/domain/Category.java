package cn.itcast.goods.category.domain;

import java.util.List;

/**
 * 分类模块实体类v  ,双向自身关联
 * @author yanke
 *
 */
public class Category {
	public List<Category> getChildren() {
		return children;
	}
	public void setChildren(List<Category> children) {
		this.children = children;
	}
	private String cid;    //主键
	private String cname;  //分类名称
	private Category parent;  //父类的分类注意因为Category类型不能叫pid
	private List<Category> children;
	
	@Override
	public String toString() {
		return "Category [cid=" + cid + ", cname=" + cname + ", parent=" + parent + ", children=" + children + ", desc="
				+ desc + "]";
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	private String desc;   //分类描述
}
