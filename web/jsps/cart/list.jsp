<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>cartlist.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script src="<c:url value='/js/round.js'/>"></script>
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/cart/list.css'/>">
<script type="text/javascript">

	$(function () {
		showTotal();   //计算总价
		/*
		* 全选添加事件,所有条目复选框与全选框状态同步
		* 重新计算总计
		* */
		$("#selectAll").click(function () {
			var bool = $("#selectAll").attr("checked");
			setItemCheckBox(bool);
			setJiesuan(bool);
        });
		showTotal();
		/*
		* 给所有条目加click事件
		* */
		$(":checkbox[name=checkboxBtn]").click(function () {
			var all = $(":checkbox[name=checkboxBtn]").length;  //所有条目的个数
			var select = $(":checkbox[name=checkboxBtn][checked=true]").length;//所有被选择的条目
			if(all == select){
			    $("#selectAll").attr("checked",true);   //勾选全选复选框
			    setJiesuan(true);  //让结算按钮有效
				//showTotal();          //重新计算总价
			}else if (select == 0){ //一个都没选中
                $("#selectAll").attr("checked",false);
                setJiesuan(false);
                //showTotal();
            }else{
                $("#selectAll").attr("checked",false);
                setJiesuan(true);
                //showTotal();
			}
			showTotal();  //都需要重新计算直接调外面
        });
		$(".jian").click(function () {     //通过条目id获取数量的id
			var id= $(this).attr("id").substring(0,32);
			var quantity = $("#"+id+"Quantity").val();  //获取输入框的数量
			//判断当前数量是否为1，为1就是删除
			if(quantity == 1){
			    if(confirm("您是否删除该条目")){
			        location ="/goods/CartItemServlet?method=batchDelete&cartItemIds="+id;
				}
			}else{
			  //  alert("bie");
			    sendUpdateQuantity(id,Number(quantity)-1);
			}
        });
		$(".jia").click(function () {
			var id = $(this).attr("id").substring(0,32);
			var quantity = $("#"+id+"Quantity").val();
			//注意数据类型
			sendUpdateQuantity(id,Number(quantity)+1);  //先转化再数值操作
        });
    });

	//请求服务器，修改数量
	function sendUpdateQuantity(id,quantity) {
		$.ajax({
		    async:false,
			cache:false,
			url:"/goods/CartItemServlet",
			data:{method:"updateQuantity",cartItemId:id,quantity:quantity},
			type:"POST",        //可以用alert测试一下。
			dataType:"json",   //data单词不能打错否则传回来的是undefined类型，
			success:function(result){
				//修改数量

				// alert(result.subtotal);
				$("#"+id+"Quantity").val(result.quantity);//传来字符串，json解析为对象
				//修改小计
				$("#"+id+"Subtotal").text(result.subtotal); //注意text，不是val，两者区别！！
				//重新计算总计
				showTotal();
            }
		});
    }

    /**
	 *计算总计
     */
    function showTotal(){
        var total = 0;
        //获取选中的复选框
        $(":checkbox[name=checkboxBtn][checked=true]").each(function () {//checked=true不是checked=checked???
			//获取复选框的值即cartItemId，通过前缀找到小计，获取其文本框值
			var id = $(this).val();

			var text = $("#"+id+"Subtotal").text();

			total+=Number(text);  //累计相加Number

			//total = accAdd(total,text)
        });
       // alert(total);
        $("#total").text(round(total,2));  //把total保留两位小数

	}
	/**
	 * js调整精度 貌似无效！！！查看round.js的处理方法。
	 */
	function accAdd(arg1,arg2){
        var r1,r2,m;
        try{r1=arg1.toString().split(".")[1].length()}catch(e) {r1=0};
        try {r2=arg1.toString().split(".")[1].length()}catch(e){r2=0};
        m = Math.pow(10,Math.max(r1,r2));
		return (arg1*m+arg2*m)/m;

	}

    /**
	 * 统一设置所有条目的复选按钮
     */
    function setItemCheckBox(bool) {
        $(":checkbox[name=checkboxBtn]").attr("checked",bool);

    }
    /**
	 * 设置结算按钮
     */
    function  setJiesuan(bool) {
        if(bool){
            $("#jiesuan").removeClass("kill").addClass("jiesuan");
            $("#jiesuan").unbind("click");                        //撤销当前元素所有click事件
		}else{
            $("#jiesuan").removeClass("jiesuan").addClass("kill");//换了样式但是还有用
			$("#jiesuan").click(function () {
				return false;
            });
		}
		
    }
    function batchDelete() {
        //获取被选中的复选框
		//创建个数组，把所有复选框的值添加到数组中，toString
		//指定location为CartItemServlet 参数为method=batchDelete，cartItemIds=数组tostring。
		var cartItemIdArray = new Array();
		$(":checkbox[name=checkboxBtn][checked=true]").each(function () {
		    cartItemIdArray.push($(this).val());
        });
		location ="/goods/CartItemServlet?method=batchDelete&cartItemIds="+cartItemIdArray;//自动调用toString方法

    }
	function jiesuan() {    //获取所有被选中条目的id，用字符串形式带到后台,最后提交表单
		var cartItemIdArray = new Array;
		$(":checkbox[name=checkboxBtn][checked=true]").each(function () {
		    cartItemIdArray.push($(this).val());
        })
		$("#cartItemIds").val(cartItemIdArray.toString());//放到表单的hidden字段中
		//把总计参数带到后台。
		$("#hiddenTotal").val($("#total").text());
		$("#jieSuanForm").submit();
    }
</script>
  </head>
  <body>
	<c:choose>
		<c:when test="${empty cartItemList}">
			<table width="95%" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td align="right">
						<img align="top" src="<c:url value='/images/icon_empty.png'/>"/>
					</td>
					<td>
						<span class="spanEmpty">您的购物车中暂时没有商品</span>
					</td>
				</tr>
			</table>
		</c:when>
		<c:otherwise>





<br/>
<br/>


<table width="95%" align="center" cellpadding="0" cellspacing="0">
	<tr align="center" bgcolor="#efeae5">
		<td align="left" width="50px">
			<input type="checkbox" id="selectAll" checked="checked"/><label for="selectAll">全选</label>
		</td>
		<td colspan="2">商品名称</td>
		<td>单价</td>
		<td>数量</td>
		<td>小计</td>
		<td>操作</td>
	</tr>


<c:forEach items="${cartItemList}" var="cartItem">

	<tr align="center">
		<td align="left">
			<input value="${cartItem.cartItemId}" type="checkbox" name="checkboxBtn" checked="checked"/>
		</td>
		<td align="left" width="70px">
			<a class="linkImage" href="<c:url value='/jsps/book/desc.jsp'/>"><img border="0" width="54" align="top" src="<c:url value='${cartItem.book.image_b}'/>"/></a>
		</td>
		<td align="left" width="400px">
		    <a href="<c:url value='/jsps/book/desc.jsp'/>"><span>${cartItem.book.bname}</span></a>
		</td>
		<td><span>&yen;<span class="currPrice" >${cartItem.book.price}</span></span></td>
		<td>
			<a class="jian" id="${cartItem.cartItemId}Jian"></a><input class="quantity" readonly="readonly" id="${cartItem.cartItemId}Quantity" type="text" value="${cartItem.quantity}"/><a class="jia" id="${cartItem.cartItemId}Jia"></a>
		</td>
		<td width="100px">
			<span class="price_n">&yen;<span class="subTotal" id="${cartItem.cartItemId}Subtotal">${cartItem.subtotal}</span></span>
		</td>
		<td>
			<a href="<c:url value='/CartItemServlet?method=batchDelete&cartItemIds=${cartItem.cartItemId}'/>">删除</a>
		</td>
	</tr>


</c:forEach>


	<tr>
		<td colspan="4" class="tdBatchDelete">
			<a href="javascript:batchDelete();">批量删除</a>
		</td>
		<td colspan="3" align="right" class="tdTotal">
			<span>总计：</span><span class="price_t">&yen;<span id="total"></span></span>
		</td>
	</tr>
	<tr>
		<td colspan="7" align="right">  <%--点击结算，触发函数，将下面的表单提交，传参数到后台--%>
			<a href="javascript:jiesuan()" id="jiesuan" class="jiesuan"></a>
		</td>
	</tr>
</table>
	<form id="jieSuanForm" action="<c:url value='CartItemServlet'/>" method="post">
		<input type="hidden" name="cartItemIds" id="cartItemIds"/>
			<%--表单的value是一个动态的，js赋值--%>
		<input type="hidden" name="method" value="loadCartItems"/>
		    <%--把总计带到下一个页面，js在提交前赋值--%>
		<input type="hidden" name="total" id="hiddenTotal"/>
	</form>

		</c:otherwise>
	</c:choose>
  </body>
</html>
