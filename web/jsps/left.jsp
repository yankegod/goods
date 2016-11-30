<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>left</title>
    <base target="body"/>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/menu/mymenu.js'/>"></script>
	<link rel="stylesheet" href="<c:url value='/menu/mymenu.css'/>" type="text/css" media="all">
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/left.css'/>">
<script language="javascript">

/*
 * 对象名必须与第一个参数相同。
 	第二个参数是显示在菜单上的大标题
 */
 
var bar = new Q6MenuBar("bar", "传智播客网上书城");  //小工具：/menu/mymenu.js
$(function() {
	bar.colorStyle = 4;   //制定配色样式一共0，1,2,3,4
	bar.config.imgDir = "<c:url value='/menu/img/'/>";//小工具所需图片的路径“+”“-”
	bar.config.radioButton=true;                      //多个一级分类是否排斥！！
/**
 * 
 一级分类、二级分类、二级分类的url、链接的内容在哪个框架页中显示。
 第一次出现一级分类时才会创建一级分类
 */
<c:forEach items="${parents}" var="parent">
	<c:forEach items="${parent.children}" var="child">
	bar.add("${parent.cname}", "${child.cname}", "/goods/BookServlet?method=findByCategory&cid=${child.cid}", "body");//要取出名字！！！
	</c:forEach>
</c:forEach>
	$("#menu").html(bar.toString());

});
</script>
</head>
  
<body>  
  <div id="menu"></div>
</body>
</html>
