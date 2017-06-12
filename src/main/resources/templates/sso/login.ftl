<#include "inc/inc_header.ftl">

<link rel="stylesheet" href="${request.contextPath}/resources/css/sso.css">

<form class="cls_form-signin" method="post" autocomplete="off"
		action="${request.contextPath}/sso/login">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	<!-- 记住我 -->
	<input type="hidden" name="remember-me" value="true"/>
	<!-- 标题 -->
	<div class="form-group">
		<h3>系统登录</h3>
	</div>
	<!-- 用户名 -->
	<div class="form-group">
		<input type="text" class="form-control" id="id_username" name="username" placeholder="请输入用户名" autofocus>
	</div>
	<!-- 密码 -->
	<div class="form-group">
		<input type="password" autocomplete="off" class="form-control" id="id_password" name="password" placeholder="请输入密码">
	</div>
	<!-- 登录按钮 -->
	<div class="form-group">
		<button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
	</div>
	<!-- 创建账号 -->
    <h5 class="text-center"><a href="${request.contextPath}/sso/registration">创建一个账号</a></h5>
</form>

<#include "inc/inc_footer.ftl">


