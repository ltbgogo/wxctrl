
<#include "/inc/inc_header.ftl">

<#assign page="listAccount">
<#include "/wx/home/inc_nav.ftl">

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
				<th scope="row"><a href="${request.contextPath}/wx/clientConsole/${account.id}">${account.nickName}</a></th>
				<td>${account.lastLoginDate ?string("yyyy-MM-dd HH:mm:ss")}</td>
				<td>${(account.wxMeta.metaStatus == 'loginSuccess') ?string("在线", "离线")}</td>
				<td>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle btn-xs" data-toggle="dropdown">
							<span class="glyphicon glyphicon-menu-hamburger"></span> 
						</button>
						<ul class="dropdown-menu dropdown-menu-right" role="menu">
							<li>
								<a href="${request.contextPath}/wx/clientConsole/${account.id}">控制台</a>
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
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="id_search_modal_label">添加微信机器人</h4>
			</div>
			<div class="modal-body">
				<img id="id_qrcode" class="img-responsive hidden" alt="Responsive image">
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
	$('#id_add_modal').modal('show');
	$.get("${request.contextPath}/wx/home/addAccount", function(r) {
		if (r.success) {
			//显示登录扫描码
			$("#id_qrcode").attr("src", "${request.contextPath}/wx/home/outputQrcode").removeClass("hidden");
			//检查监听状态
			setInterval(function() {
				$.get("${request.contextPath}/wx/home/checkLoginStatus", function(r) {
					if (r.content == 'loginSuccess') {
						window.location.reload();
					}
				}, "json");
			}, 2000);
		}
	}, "json");
}

</script>

<#include "inc/inc_footer.ftl">

