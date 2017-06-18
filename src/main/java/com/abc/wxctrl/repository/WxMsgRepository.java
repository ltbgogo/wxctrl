package com.abc.wxctrl.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.domain.WxMsg;

public interface WxMsgRepository extends JpaRepository<WxMsg, String> {
	
	/**
	 * 根据消息编号查找消息
	 */
	WxMsg findByMsgId(String msgId);
	
	/**
	 * 查询群会话
	 */
	@Query("select t from WxMsg t where t.account=?1 and t.groupName=?2")
	Page<WxMsg> findGroupMsgs(WxAccount account, String contactName, Pageable pageable);
	
	/**
	 * 查询单人对话
	 */
	@Query("select t from WxMsg t where t.account=?1 and t.groupName is null and t.contactName=?2")
	Page<WxMsg> findFriendMsgs(WxAccount account, String contactName, Pageable pageable);
	
	/**
	 * 查询所有会话过的组
	 */
	@Query("select distinct t.groupName from WxMsg t where t.account=?1 and t.groupName is not null order by t.groupName asc")
	List<String> findAllGroupNames(WxAccount account);
	
	/**
	 * 查询所有会话的朋友
	 */
	@Query("select distinct t.contactName from WxMsg t where t.account=?1 and t.groupName is null group by t.contactName")
	List<String> findAllFriendNames(WxAccount account);
}






