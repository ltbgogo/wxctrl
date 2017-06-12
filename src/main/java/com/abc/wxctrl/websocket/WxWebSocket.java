package com.abc.wxctrl.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.abc.wxctrl.manager.UserManager;
import com.abc.wxctrl.utility.MsgVO;
import com.abc.wxctrl.wx.WxMeta;
import com.abc.wxctrl.wx.WxMetaStorage;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

/**
 * 每个微信账号分配一个链接
 */
@Log4j
@ServerEndpoint(value = "/wx/websocket")
public class WxWebSocket {

    //concurrent包的线程安全Set，用来存放每个客户端对应的WxWebSocket对象。
    private static Set<WxWebSocket> webSocketSet = Collections.synchronizedSet(new HashSet<WxWebSocket>());

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    //微信账号
    private String wxAccountId;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        System.out.println("有新连接加入！当前连接数为：" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        log.info("有一连接关闭！当前连接数为：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        MsgVO msg = MsgVO.ofJson(message);
        
        //接收微信账号的wxAccountId
        if (msg.isCode("wx_account_id")) {
        	this.wxAccountId = msg.getContent(String.class);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error(e.getMessage(), e);
    }

    /**
     * 发送消息
     */
    @SneakyThrows
    public void sendMessage(String message) {
        this.session.getBasicRemote().sendText(message);
//        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 群发消息
     */
    @SneakyThrows
    public static void sendMessage(String wxAccountId, String message) {
        for (WxWebSocket item : webSocketSet) {
        	if (wxAccountId.equals(item.wxAccountId)) {
        		item.sendMessage(message);	
        	}
        }
    }

    public static int getOnlineCount() {
        return webSocketSet.size();
    }
}


