package com.abc.test.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Log4j
@ServerEndpoint(value = "/wx/websocket")
@Component
public class WxWebSocket {
	
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的WxWebSocket对象。
    private static Set<WxWebSocket> webSocketSet = Collections.synchronizedSet(new HashSet<WxWebSocket>());

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    //微信账号
    private String uin;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        increaseOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        decreaseOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        if (message.startsWith("uin:")) {
        	this.uin = message.split(":")[1];
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
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 群发消息
     */
    public static void sendAllMessage(String message) throws IOException {
        for (WxWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
            	log.error(e.getMessage(), e);
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void increaseOnlineCount() {
        WxWebSocket.onlineCount++;
    }

    public static synchronized void decreaseOnlineCount() {
        WxWebSocket.onlineCount--;
    }
}

