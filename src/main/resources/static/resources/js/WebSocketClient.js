
function WebSocketClient(config) {
    this.websocket = new WebSocket("ws://localhost/wx/websocket");
	//连接发生错误的回调方法
    this.websocket.onerror = config.onerror || $.noop;
    //连接成功建立的回调方法
    this.websocket.onopen = config.onopen || $.noop;
    //接收到消息的回调方法
    this.websocket.onmessage = config.onmessage || $.noop;
    //连接关闭的回调方法
    this.websocket.onclose = config.onclose || $.noop;
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    this.websocket.onbeforeunload = function(){
        this.websocket.close();
    };
    
    /**
     * 发送消息
     */
    this.sendMessage = function(code, content) {
    	
    };
}

WebSocketClient.init = function(config) {
	
};