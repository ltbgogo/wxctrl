
<#include "inc/inc_header.ftl">

<#assign page="listHistory">
<#include "/wx/account/inc_nav.ftl">

<link rel="stylesheet" href="${request.contextPath}/resources/css/emoji.css">

<div class="row cls_page_body">
	<!-- 联系人列表 -->
	<div class="col-xs-3">
		<div id="id_contact_list">
			<!-- 群聊列表面板 -->
			<div class="panel panel-primary cls_group">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-cloud"></span> 群聊列表</h3>
				</div>
				<div class="list-group" style="overflow-y: scroll;">
					<#list allGroupNames as name>
						<a href="javascript: void(0);" class="list-group-item" attr-type="group" attr-name="${name ?html}" style="outline: none;">
							<span class="cls_name">${name}</span>
						</a>
					</#list>
				</div>
			</div>
			<!-- 朋友列表面板 -->
			<div class="panel panel-primary cls_friend">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-user"></span> 朋友列表</h3>
				</div>
				<div class="list-group" style="overflow-y: scroll;">
					<#list allFriendNames as name>
						<a href="javascript: void(0);" class="list-group-item" attr-type="friend" attr-name="${name ?html}" style="outline: none;">
							<span class="cls_name">${name}</span>
						</a>
					</#list>
				</div>
			</div>
		</div>
	</div>
	<!-- 消息面板 -->
	<div class="col-xs-9">
		<!-- 消息列表 -->
		<ul id="id_msg_list" style="border: 1px solid rgb(221, 221, 221); padding: 5px; overflow-y: scroll;"></ul>
		<!-- 分页按钮 -->
			<ul class="pager">
				<li >
					<a href="? ">上一页</a>
				</li>
				<li class=" ">
					<a href="? ">下一页</a>
				</li>
			</ul>
	</div>
</div>

<script type="text/javascript">

/**
 * 调整面板高度
 */
function adjustHeight() {
	$("#id_contact_list .list-group").height((window.innerHeight - 180) / 2);
	$("#id_msg_list").height(window.innerHeight - 135);
}

/**
 * 展示历史消息
 */
function showMsgList() {
	$($g.contactATag).siblings().removeClass("list-group-item-info");
	$($g.contactATag).addClass("list-group-item-info");
	$.get("${request.contextPath}/wx/showMsgList", {
		wxAccountId: "${account.id}", 
		type: $($g.contactATag).attr("attr-type"), 
		contactName: $($g.contactATag).find(".cls_name").text(),
		_: new Date().getTime()
	}, function(data) {
		$("#id_msg_list").empty();
		$.each(data.result.content, function() {
			var li = $('<li><b></b>&nbsp;<label></label><p></p></li>').appendTo("#id_msg_list");
			//假如没有发送人，那么就是“我”发送的消息
			if (this.fromContactNickName === null) {
				this.fromContactNickName = "我";
				li.addClass("text-right")
			} else {
				li.addClass("text-left");
			}
			li.find("label").html(this.fromContactNickName);
			li.find("b").text(this.createTime);
			//假如是图片消息
			if (this.msgType == 3) {
				li.find("p").html('<img style="width: 200px;" src="${request.contextPath}/wx_msg_img/' + this.msgId + '.jpg"/>');
			} //否则就是文本消息 
			else {
				li.find("p").text(this.content)	
			}
		});
		
		$("#id_msg_list").scrollTop($("#id_msg_list").get(0).scrollHeight);
	}, "json");
}

/**
 * 入口函数
 */
$(function() {
	//调整面板高度
	adjustHeight();
	$(window).resize(adjustHeight);
});

</script>

<#include "inc/inc_footer.ftl">

