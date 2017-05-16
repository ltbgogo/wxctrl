
<#include "inc/inc_header.ftl">

<#assign page="listClients">
<#include "inc/inc_nav.ftl">

<table class="table table-hover cls_dg_center">
	<thead>
		<tr>
			<th>微信账号</th>
			<th>登录时间</th>
			<th>是否在线</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<#list r.result as account>
			<tr>
				<th scope="row"><a href="${app.contextPath}/wx/clientConsole/${account.id}">${account.nickName}</a></th>
				<td>${account.lastLoginDate ?string("yyyy-MM-dd HH:mm:ss")}</td>
				<td>${account.isOnline ?string("在线", "离线")}</td>
				<td>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle btn-xs" data-toggle="dropdown">
							<span class="glyphicon glyphicon-menu-hamburger"></span> 
						</button>
						<ul class="dropdown-menu dropdown-menu-right" role="menu">
							<li>
								<a href="${app.contextPath}/wx/clientConsole/${account.id}">控制台</a>
							</li>
						</ul>
					</div>
				</td>
			</tr>
		</#list>
	</tbody>
</table>

<script type="text/javascript">


</script>

<#include "inc/inc_footer.ftl">

