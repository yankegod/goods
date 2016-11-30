package cn.itcast.goods.page;

import java.util.List;
/**
 * 分页bean在各层之间传递，从页面所需入手
 * @author yanke
 *
 * @param <T>
 */
public class PageBean<T> {
	private int pc;     //当前页码
	private int tr;     //共记录数
	private int ps;     //每页记录数
	private String url; //请求路径和参数如：/BookServlet?method=findxxx&cid=1&bname=2
	private List<T> beanList;
	
	public int getTp(){    //计算总页数
		int tp = tr/ps;
		return tr%ps == 0? tp : tp+1;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<T> getBeanList() {
		return beanList;
	}
	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}
	@Override
	public String toString() {
		return "PageBean [pc=" + pc + ", tr=" + tr + ", ps=" + ps + ", url=" + url + ", beanList=" + beanList + "]";
	}
	
}
