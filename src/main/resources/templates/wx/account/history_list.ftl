
<#include "inc/inc_header.ftl">

<#assign page="listHistory">
<#include "/wx/account/inc_nav.ftl">

<link rel="stylesheet" href="${request.contextPath}/resources/css/emoji.css">
<style>
#id_msg_list {
	border: 1px solid rgb(221, 221, 221); overflow-y: scroll;
	margin: 0;
	padding: 0;
}
#id_msg_list li {
	padding: 5px;
	margin: 0;
	list-style: none;
}
.cls_li_msg_active {
	background-color: #F5F5F5;
}
</style>

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
						<a href="javascript: void(0);" onclick="refreshMsgList(this);" class="list-group-item" attr-type="group" attr-name="${name ?html}" style="outline: none;">
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
						<a href="javascript: void(0);" onclick="refreshMsgList(this);" class="list-group-item" attr-type="friend" attr-name="${name ?html}" style="outline: none;">
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
		<ul id="id_msg_list"></ul>
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
 * 全局变量
 */
var $g = {
	params: {
		accountId: "${account.id}", 
		contactType: null,
		contactName: null,
		page: 0	
	}
};

/**
 * 刷新消息列表
 */
function refreshMsgList(target) {
	$("#id_contact_list .list-group-item-info").removeClass("list-group-item-info");
	$(target).addClass("list-group-item-info");
	$.extend($g.params, {
		contactType: $(target).attr("attr-type"),
		contactName: $(target).attr("attr-name"),
		page: 0
	});
	showMsgList();
}

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
	$.get("${request.contextPath}/wx/account/history/showMsgList?_=" + new Date().getTime(), $g.params, function(data) {
		$("#id_msg_list").empty();
		$.each(data.content.content, function() {
			var li = $('<li><b></b>&nbsp;<label></label><p></p></li>').appendTo("#id_msg_list");
			//事件绑定
			li.mouseover(function() {
				$(this).addClass("cls_li_msg_active");
			}).mouseout(function() {
				$(this).removeClass("cls_li_msg_active");
			});
			//假如没有发送人，那么就是“我”发送的消息
			if (this.isMyEcho) {
				this.contactName = "我";
				li.addClass("text-right")
			} else {
				li.addClass("text-left");
			}
			li.find("label").html(this.contactName);
			li.find("b").text(this.createTime);
			//假如是图片消息
			if (this.msgType == 3) {
				li.find("p").html('<img style="width: 200px;" src="${request.contextPath}/wx_resources/msg_img/' + this.msgId.replace(/^((..)(..)(..).+)$/, "/$2/$3/$4/$1") + '.jpg"/>');
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

