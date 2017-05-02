
<#include "inc/inc_header.ftl">

<#assign page="addClient">
<#include "inc/inc_nav.ftl">

<div id="id_jumbotron" class="jumbotron">
  <h1>添加微信机器人</h1>
  <p>请确保要添加的微信机器人还没有被添加！</p>
  <p><a class="btn btn-primary btn-lg" href="javascript: addClient();" role="button">获取微信登录扫描码</a></p>
</div>

<img id="id_qrcode" class="img-responsive hidden" alt="Responsive image">

<script type="text/javascript">

function addClient() {
	$("#id_jumbotron .btn").addClass("disabled");
	$.get("${app.contextPath}/wx/addClient", function(r) {
		if (r.success) {
			$("#id_jumbotron").hide();
			$("#id_qrcode").attr("src", "${app.contextPath}/wx/outputQrcode").removeClass("hidden");
		}
	}, "json");
}

</script>

<#include "inc/inc_footer.ftl">

