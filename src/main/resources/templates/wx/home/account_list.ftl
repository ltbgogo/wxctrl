
<#include "/inc/inc_header.ftl">

<#assign page="listAccount">
<#include "/wx/home/inc_nav.ftl">

<div class="cls_page_body">

<button type="button" class="btn btn-default" onclick="addClient();">添加</button>

<!-- 微信账号列表 -->
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
		<#list r.content as account>
			<tr>
				<th scope="row"><a href="${request.contextPath}/wx/account/history/listHistory?accountId=${account.id}">${account.nickName}</a></th>
				<td>${account.lastLoginDate ?string("yyyy-MM-dd HH:mm:ss")}</td>
				<td>${(account.wxMeta.metaStatus == 'loginSuccess') ?string("在线", "离线")}</td>
				<td>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle btn-xs" data-toggle="dropdown">
							<span class="glyphicon glyphicon-menu-hamburger"></span> 
						</button>
						<ul class="dropdown-menu dropdown-menu-right" role="menu">
							<li>
								<a href="${request.contextPath}/wx/account/history/listHistory?accountId=${account.id}">历史消息</a>
							</li>
						</ul>
					</div>
				</td>
			</tr>
		</#list>
	</tbody>
</table>

<!-- 添加微信账号 -->
<div class="modal fade" data-backdrop="static" id="id_add_modal" tabindex="-1" role="dialog" aria-labelledby="id_search_modal_label">
	<div class="modal-dialog modal-sm" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="id_search_modal_label">添加微信机器人</h4>
			</div>
			<div class="modal-body">
				<img id="id_qrcode" class="img-responsive hidden" alt="微信扫码登陆">
				<div id="id_qrcode_success_alert" class="alert alert-success hidden" role="alert">扫码成功，后台正在登陆，可能会花费一点时间，请耐心等待。。。</div>
			</div>
		</div>
	</div>
</div>

</div>

<script type="text/javascript">

/**
 * 全局变量
 */
var $g = {};

/**
 * 添加客户端
 */
function addClient() {
	//清理数据
	$("#id_qrcode_success_alert").addClass("hidden");
	$("#id_qrcode").attr("src", "").addClass("hidden");
	//显示面板
	$('#id_add_modal').modal('show');
	$.get("${request.contextPath}/wx/home/account/addAccount", function(r) {
		if (r.success) {
			//显示登录扫描码
			$("#id_qrcode").attr("src", "${request.contextPath}/qrcode/${app.current.id}.jpg?_=" + new Date().getTime()).removeClass("hidden");
			//检查监听状态
			setInterval(function() {
				$.get("${request.contextPath}/wx/home/account/checkLoginStatus", function(r) {
					//登陆成功，刷新页面
					if (r.content == 'loginSuccess') {
						window.location.reload();
					} //扫码成功，显示消息 
					else if (r.content == "logining") {
						$("#id_qrcode").addClass("hidden");
						$("#id_qrcode_success_alert").removeClass("hidden");
					}
				}, "json");
			}, 2000);
		}
	}, "json");
}

</script>

<#include "inc/inc_footer.ftl">

