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
			+ "where t.wxAccount=?1 and t.groupNickName=?2")
	Page<WxMsg> findGroupMsg(WxAccount wxAccount, String groupNickName, Pageable pageable);
	/**
	 * 查询单人对话
	 */
	@Query("select t from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupNickName is null "
			+ "and (t.fromContactNickName=?2 or t.toContactNickName=?2)")
	Page<WxMsg> findContactMsg(WxAccount wxAccount, String contactNickName, Pageable pageable);
	/**
	 * 查询所有会话过的组
	 */
	@Query("select t.groupNickName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupNickName is not null "
			+ "group by t.groupNickName "
			+ "order by t.groupNickName asc")
	List<String> findGroupNickNames(WxAccount account);
	/**
	 * 查询所有会话发起人
	 */
	@Query("select distinct t.toContactNickName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupNickName is null and t.toContactNickName is not null "
			+ "group by t.toContactNickName")
	List<String> findToContactNickName(WxAccount account);
	/**
	 * 查询所有会话接收人
	 */
	@Query("select distinct t.fromContactNickName from WxMsg t "
			+ "where t.wxAccount=?1 and t.groupNickName is null and t.fromContactNickName is not null "
			+ "group by t.fromContactNickName")
	List<String> findFromContactNickName(WxAccount account);
}




