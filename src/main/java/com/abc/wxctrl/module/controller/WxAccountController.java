//package com.abc.wxctrl.module.controller;
//
//import static com.abc.wxctrl.repository.RepoFactory.rf;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
//import com.abc.wxctrl.domain.WxAccount;
//import com.abc.wxctrl.utility.ResultVO;
//import com.abc.wxctrl.wx.WxMeta;
//import com.abc.wxctrl.wx.WxMetaStorage;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//@RestController
//@RequestMapping("/wx/account")
//public class WxAccountController {
//	
//    /**
//     * 客户端控制台
//     */
//    @RequestMapping("/console")
//    ModelAndView clientConsole(String accountId) {
//    	WxAccount account = rf.getWxAccountRepo().findOne(accountId);
//    	List<String> groupNickNames = rf.getWxMsgRepo().findGroupNickNames(account);
//    	List<String> contactNickNames = new ArrayList<>();
////    	for (Object o : WxMetaStorage.get(account).getFriends()) {
////    		contactNickNames.add(((JSONObject) o).getString("NickName"));
////    	}
//    	return new ModelAndView("wx/clientConsole", "account", account)
//    			.addObject("contactNickNames", contactNickNames)
//    			.addObject("groupNickNames", groupNickNames);
//    }
//    
//    /**
//     * 客户端控制台-获取对话内容
//     */
//    @RequestMapping("showMsgList")
//    ResultVO clientConsole(String wxAccountId, String type, String contactName, 
//    		@PageableDefault(value = 15, sort = {"createTime"}, direction = Direction.DESC) Pageable pageable) {
//    	WxAccount account = rf.getWxAccountRepo().findOne(wxAccountId);
//    	Page<WxMsg> page = null;
//    	if ("group".equals(type)) {
//    		page = rf.getWxMsgRepo().findGroupMsg(account, contactName, pageable);
//    	} else {
//    		page = rf.getWxMsgRepo().findContactMsg(account, contactName, pageable);
//    	}
//    	List<WxMsg> content = new ArrayList<>(page.getContent());
//		Collections.reverse(content);
//		return ResultVO.succeed(new PageImpl<>(content, pageable, page.getTotalElements()));
//    }
//    
//    /**
//     * 客户端控制台-获取对话内容
//     */
//    @RequestMapping("sendMsg")
//    ResultVO sendMsg(String wxAccountId, String type, String nickName, String msg) {
//    	WxMeta meta = WxMetaStorage.get(wxAccountId);
//    	if (meta == null) {
//    		return ResultVO.fail("账号处于离线状态！");
//    	} else {
//    		boolean isGrpMsg = "group".equals(type);
////    		JSONArray members = isGrpMsg ? meta.getGroups() : meta.getFriends();
////    		for (int i = 0; i < members.size(); i++) {
////    			JSONObject member = members.getJSONObject(i);
////    			if (nickName.equals(member.getString("NickName"))) {
////    				meta.getHttpClient().webwxsendmsg(msg, member.getString("UserName"));
////    				//持久化消息到数据库
////    				if (isGrpMsg) {
////    					meta.getMsg2DB().handleMe2Grp(null, 1, nickName, msg, new Date());
////    				} else {
////    					meta.getMsg2DB().handleMe2Contact(null, 1, nickName, msg, new Date());
////    				}
////    			}
////    		}
//    		return ResultVO.SUCCESS;
//    	}
//    }
//}
//
//
//
//
//
//
