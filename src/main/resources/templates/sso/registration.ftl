<#include "inc/inc_header.ftl">

<link rel="stylesheet" href="${request.contextPath}/resources/css/sso.css">

<form method="POST" class="cls_form-signin" autocomplete="off">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	<div class="form-group">
		<h3 class="cls_form-signin-heading">创建账号</h3>
	</div>
	<!-- 用户名 -->
	<div class="form-group">
		<input type="text" class="form-control" name="username" placeholder="请输入用户名" autofocus/>
	</div>
	<!-- 密码 -->
	<div class="form-group">
		<input type="password" class="form-control" name="password" placeholder="请输入密码"/>
	</div>
	<!-- 注册按钮 -->
	<div class="form-group">
		<button class="btn btn-lg btn-primary btn-block" type="submit">创建</button>
	</div>
	<!-- 登录 -->
	<h5 class="text-center"><a href="${request.contextPath}/sso/login">已经有账号</a></h5>
</form>

<#include "inc/inc_footer.ftl">
