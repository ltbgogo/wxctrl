package com.abc.test.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abc.test.domain.WxAccount;
import com.abc.test.domain.WxMsg;

public interface WxMsgRepository extends JpaRepository<WxMsg, String> {
	
	/**
	 * 根据消息编号查找消息
	 */
	WxMsg findByMsgId(String msgId);
	
	/**
	 * 查询群会话
	 */
	@Query("select t from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupName=?2")
	Page<WxMsg> findGroupMsg(WxAccount wxAccount, String groupName, Pageable pageable);
	/**
	 * 查询单人对话
	 */
	@Query("select t from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupName is null "
			+ "and (t.fromUserName=?2 or t.toUserName=?2)")
	Page<WxMsg> findContactMsg(WxAccount wxAccount, String contactName, Pageable pageable);
	/**
	 * 查询所有会话过的组
	 */
	@Query("select t.groupName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupName is not null "
			+ "group by t.groupName "
			+ "order by t.groupName asc")
	List<String> findGroupNames(WxAccount account);
	/**
	 * 查询所有会话发起人
	 */
	@Query("select distinct t.toUserName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupName is null and t.toUserName is not null "
			+ "group by t.toUserName")
	List<String> findToUserName(WxAccount account);
	/**
	 * 查询所有会话接收人
	 */
	@Query("select distinct t.fromUserName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupName is null and t.fromUserName is not null "
			+ "group by t.fromUserName")
	List<String> findFromUserName(WxAccount account);
}




