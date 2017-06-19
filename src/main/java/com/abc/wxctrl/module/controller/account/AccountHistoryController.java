package com.abc.wxctrl.module.controller.account;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.domain.WxMsg;
import com.abc.wxctrl.utility.ResultVO;
import com.abc.wxctrl.wx.WxMeta;
import com.abc.wxctrl.wx.WxMetaStorage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/wx/account/history")
public class AccountHistoryController {
	
    /**
     * 转向到历史面板
     */
    @RequestMapping("/listHistory")
    ModelAndView listHostory(String accountId) {
    	WxAccount account = rf.getWxAccountRepo().findOne(accountId);
    	return new ModelAndView("wx/account/history_list", "account", account)
    			.addObject("allFriendNames", rf.getWxMsgRepo().findAllFriendNames(account))
    			.addObject("allGroupNames", rf.getWxMsgRepo().findAllGroupNames(account));
    }
    
    /**
     * 获取对话内容
     */
    @RequestMapping("sendMsg")
    ResultVO sendMsgHistory(String wxAccountId, String type, String nickName, String msg) {
    	WxMeta meta = WxMetaStorage.get(wxAccountId);
    	if (meta == null) {
    		return ResultVO.fail("账号处于离线状态！");
    	} else {
    		boolean isGrpMsg = "group".equals(type);
//    		JSONArray members = isGrpMsg ? meta.getGroups() : meta.getFriends();
//    		for (int i = 0; i < members.size(); i++) {
//    			JSONObject member = members.getJSONObject(i);
//    			if (nickName.equals(member.getString("NickName"))) {
//    				meta.getHttpClient().webwxsendmsg(msg, member.getString("UserName"));
//    				//持久化消息到数据库
//    				if (isGrpMsg) {
//    					meta.getMsg2DB().handleMe2Grp(null, 1, nickName, msg, new Date());
//    				} else {
//    					meta.getMsg2DB().handleMe2Contact(null, 1, nickName, msg, new Date());
//    				}
//    			}
//    		}
    		return ResultVO.SUCCESS;
    	}
    }
    
    
    /**
     * 获取对话内容
     */
    @RequestMapping("showMsgList")
    ResultVO showMsgList(String accountId, String contactType, String contactName, @PageableDefault(value = 50, sort = {"createTime"}, direction = Direction.DESC) Pageable pageable) {
    	WxAccount account = rf.getWxAccountRepo().findOne(accountId);
    	Page<WxMsg> page = null;
    	if ("group".equals(contactType)) {
    		page = rf.getWxMsgRepo().findGroupMsgs(account, contactName, pageable);
    	} else {
    		page = rf.getWxMsgRepo().findFriendMsgs(account, contactName, pageable);
    	}
    	List<WxMsg> content = new ArrayList<>(page.getContent());
		Collections.reverse(content);
		return ResultVO.succeed(new PageImpl<>(content, pageable, page.getTotalElements()));
    }
}






