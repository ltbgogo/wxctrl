<#include "inc/inc_header.ftl">

<style>
	#id_msg_list {border: 1px solid rgb(211, 211, 211); padding: 5px; height: 200px; overflow: scroll;}	
	#id_msg_sender textarea {width: 100%; resize: none; border: 1px solid rgb(211, 211, 211); height: 80px;}
	#id_back_btn {margin: 5px 0;}
</style>

<a id="id_back_btn" href="${app.contextPath}/wx/listClients" class="btn btn-default">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
</a>

<div class="row">
	<!-- 联系人列表 -->
	<div class="col-xs-3">
		<div id="id_contact_list" class="list-group">
			<#list groupNames as groupName>
				<a href="javascript: void(0);" class="list-group-item" attr-type="group">${groupName ?html}</a>
			</#list>
			<#list individuals as individual>
				<a href="javascript: void(0);" class="list-group-item" attr-type="individual">${individual ?html}</a>
			</#list>
		</div>
	</div>
	<!-- 消息面板 -->
	<div class="col-xs-9">
		<!-- 消息列表 -->
		<ul id="id_msg_list"></ul>
		<br />
		<!-- 消息发送面板 -->
		<div id="id_msg_sender">
			<textarea></textarea>
			<button id="id_send_msg_btn" onclick="sendMsg();" class="btn btn-default">发送</button>
		</div>
	</div>
</div>

<script type="text/javascript">

/**
 * 全局变量
 */
var $g = {
	contactATag: null	
};

/**
 * 发送消息
 */
function sendMsg() {
	var msg = $("#id_msg_sender textarea").val();
	console.log(msg);
}

/**
 * 刷新消息列表
 */
function refreshMsgList() {
	if ($g.contactATag) {
		$($g.contactATag).siblings().removeClass("list-group-item-info");
		$($g.contactATag).addClass("list-group-item-info");
		$.get("${app.contextPath}/wx/showMsgList", {
			wxAccountId: "${account.id}", 
			type: $($g.contactATag).attr("attr-type"), 
			contactName: $($g.contactATag).text(),
			_: new Date().getTime()
		}, function(data) {
			$("#id_msg_list").empty();
			$.each(data.result.content, function() {
				var p = $('<li><b></b>&nbsp;<label></label><p></p></li>').appendTo("#id_msg_list");
				if (this.fromUserName === null) {
					this.fromUserName = "我";
					p.addClass("text-right")
				} else {
					p.addClass("text-left");				
				}
				p.find("label").html(this.fromUserName);
				p.find("b").text(this.createTime);
				p.find("p").text(this.content)
			});
			
			$("#id_msg_list").scrollTop($("#id_msg_list").get(0).scrollHeight);
		}, "json");
	}
}

/**
 * 执行入口
 */
$(function() {
	//设置页面title
	document.title = '${account.nickName} - ${account.isOnline ?string("在线", "离线")}';
	//发送消息快捷键监听
	Mousetrap($("#id_msg_sender textarea").get(0)).bind('ctrl+enter', sendMsg);
	//调整消息框高度
	$("#id_msg_list").height($("body").get(0).scrollHeight - 200); 
	//查看联系人消息
	$("#id_contact_list a").on("click", function() {
		$g.contactATag = this;
		refreshMsgList();
	});
});

</script>

<#include "inc/inc_footer.ftl">

