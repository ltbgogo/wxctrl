
<!-- 导航栏 -->
<ul id="id_navbar" class="nav nav-tabs navbar-fixed-top" role="tablist">
	<li role="presentation"><a href="${request.contextPath}/wx/home/account/listAccount" role="tab">主页</a></li>
	<li role="presentation" class="${(page == 'listHistory') ?string('active', '')}"><a href="${request.contextPath}/wx/account/history/listHistory?accountId=${account.id}" role="tab">历史消息</a></li>
</ul>






