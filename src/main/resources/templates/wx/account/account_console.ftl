<#include "inc/inc_header.ftl">

<style>
	#id_msg_list {border: 1px solid rgb(211, 211, 211); padding: 5px; height: 200px; overflow: scroll;}	
	#id_msg_sender textarea {width: 99%; resize: none; border: 1px solid rgb(211, 211, 211); height: 80px;}
	#id_btn_grp {margin: 5px 0;}
</style>

<!-- 操作按钮组 -->
<div id="id_btn_grp" class="btn-group" role="group" aria-label="...">
	<!-- 回退按钮 -->
	<a href="${request.contextPath}/wx/listClients" class="btn btn-default">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	</a>
	<!-- 刷新页面按钮 -->
	<a href="javascript: location.reload(true);" class="btn btn-default">
		<span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
	</a>
</div>

<div class="row">
	<!-- 联系人列表 -->
	<div class="col-xs-3">
		<div id="id_contact_list" class="list-group">
			<#list groupNickNames as name>
				<a href="javascript: void(0);" class="list-group-item" attr-type="group">
					<span class="glyphicon glyphicon-cloud"></span> <span class="cls_name">${name ?html}</span>
				</a>
			</#list>
			<#list contactNickNames as name>
				<a href="javascript: void(0);" class="list-group-item" attr-type="individual">
					<span class="glyphicon glyphicon-user"></span> <span class="cls_name">${name ?html}</span>
				</a>
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
			<div class="input-group">
				<textarea placeholder="快捷键 “ctrl + 回车” 发送消息"></textarea>
				<span class="input-group-btn">
					<button id="id_send_msg_btn" onclick="sendMsg();" class="btn btn-default">发送</button>
				</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

/**
 * 全局变量
 */
var $g = {
	contactATag: null,	
	websocket: null
};

/**
 * 发送消息
 */
function sendMsg() {
	var msg = $("#id_msg_sender textarea").val();
	if ($.trim(msg)) {
		$("#id_msg_sender textarea").val('');
		$.post("${request.contextPath}/wx/sendMsg", {
			wxAccountId: "${account.id}", 
			type: $($g.contactATag).attr("attr-type"), 
			nickName: $($g.contactATag).find(".cls_name").text(),
			msg: msg,
			_: new Date().getTime()	
		}, "json");
	}
}

/**
 * 刷新消息列表
 */
function refreshMsgList() {
	if ($g.contactATag) {
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
}

/**
 * 监听消息
 */
function startWebsocket() {
    //判断当前浏览器是否支持WebSocket
    if('WebSocket' in window){
        $g.websocket = new WebSocket("ws://localhost/wx/websocket");
    }
    else{
        alert('Not support websocket')
    }

    //连接发生错误的回调方法
    $g.websocket.onerror = function() {};

    //连接成功建立的回调方法
    $g.websocket.onopen = function(event) {
    	$g.websocket.send("wxAccountId:${account.id}");
    };

    //接收到消息的回调方法
    $g.websocket.onmessage = function(event) {
    	if (event.data == "sync") {
    		refreshMsgList();
    	}
    };

    //连接关闭的回调方法
    $g.websocket.onclose = function() {};

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    $g.websocket.onbeforeunload = function(){
        websocket.close();
    };
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
	$(window).resize(function() { $("#id_msg_list").height($("body").get(0).scrollHeight - 200); });
	//查看联系人消息
	$("#id_contact_list a").on("click", function() {
		$g.contactATag = this;
		refreshMsgList();
	});
	//启动消息同步监听
	startWebsocket();
});

</script>

<#include "inc/inc_footer.ftl">

