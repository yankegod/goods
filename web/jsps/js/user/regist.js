$(function() {
	/*
	 * 1. 得到所有的错误信息，循环遍历之。调用一个方法来确定是否显示错误信息！
	 */
	$(".errorClass").each(function() {
		showError($(this));//遍历每个元素，使用每个元素来调用showError方法
	});
	
	/*
	 * 2. 切换注册按钮的图片
	 */
	$("#submitBtn").hover(
		function() {
			$("#submitBtn").attr("src", "/goods/images/regist2.jpg");
		},
		function() {
			$("#submitBtn").attr("src", "/goods/images/regist1.jpg");
		}
	);
	
	$(".inputClass").focus(function(){
		var labelId = $(this).attr("id")+"Error";
		$("#"+labelId).text("");
		showError($("#"+labelId));
	});
	
	$(".inputClass").blur(function(){
		  var id = $(this).attr("id");
		  var funName = "validate"+id.substring(0,1).toUpperCase()+id.substring(1)+"()";
		  eval(funName);
	});
	
	$("#registForm").submit(function(){
		var bool =true;
		
		if(!validateLoginname()){
			return false;
		}
		if(!validateLoginpass()){
			return false;
		}
		if(!validateReloginpass()){
			return false;
		}
		if(!validateEmail()){
			return false;
		}
		if(!validateVerifyCode()){
			return false;
		}
		
		return bool;
	});
});

function validateLoginname(){  // 非空、长度、是否注册
	var id = "loginname";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("用户名不能为空！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	if(value.length<3 || value.length>8 ){       //length单词不能写错！！！
		$("#"+id+"Error").text("长度在3到8之间！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	$.ajax({
		url:"/goods/UserServlet",       //要请求的servlet
		data:{method:"ajaxValidataLoginname",loginname:value},  //给服务器的参数
		type:"post",  
		dataType:"json",
		async:false,        //true会单独开一个线程，没等服务器返回结果就下面返回true
		cache:false,
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("用户名已被注册");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});

	return true;
	
}
function validateLoginpass(){
	var id = "loginpass";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("密码不能为空！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	if(value.length<3 || value.length>8 ){       //length单词不能写错！！！
		$("#"+id+"Error").text("密码长度在3到8之间！");
		showError($("#"+id+"Error"));
		return false;
	}
	return true;
}
function validateReloginpass(){
	var id = "reloginpass";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("确认密码不能为空！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	if(value !=$("#loginpass").val() ){       //length单词不能写错！！！
		$("#"+id+"Error").text("两次密码不同！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	return true;
	
}
function validateEmail(){
	var id = "email";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("email不能为空！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	if(!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value) ){       //length单词不能写错！！！
		$("#"+id+"Error").text("格式不对！！！");
		showError($("#"+id+"Error"));
		return false;
	}
	$.ajax({
		url:"/goods/UserServlet",       //要请求的servlet
		data:{method:"ajaxValidataEmail",email:value},  //给服务器的参数
		type:"post",  
		dataType:"json",
		async:false,        //true会单独开一个线程，没等服务器返回结果就下面返回true
		cache:false,
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("email已被注册");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	return true;
}
function validateVerifyCode(){   //  把所有可变的都隔离出来！！
	var id = "verifyCode";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("验证码不能为空！！！");
		showError($("#"+id+"Error"));
		return false;
		}
	if(value.length != 4){
		$("#"+id+"Error").text("错误验证码！！！！");
		showError($("#"+id+"Error"));
		return false;
		}
	$.ajax({
		url:"/goods/UserServlet",       //要请求的servlet
		data:{method:"ajaxValidateVerifyCode",verifyCode:value},  //给服务器的参数
		type:"post",  
		dataType:"json",
		async:false,        //true会单独开一个线程，没等服务器返回结果就下面返回true
		cache:false,
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("验证码错误");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	return true;
	}
/*
 * 判断当前元素是否存在内容，如果存在显示，不页面不显示！
 */
function showError(ele) {
	var text = ele.text();//获取元素的内容
	if(!text) {//如果没有内容
		ele.css("display", "none");//隐藏元素
	} else {//如果有内容
		ele.css("display", "");//显示元素
	}
}

/*
 * 换一张验证码
 */
function _hyz(){

	$("#imgVerifyCode").attr("src", "/goods/VerifyCodeServlet?a=" + new Date().getTime());
}
