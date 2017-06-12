package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.domain.WxFriend;
import com.abc.wxctrl.domain.WxGroup;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Service
public class WxDBService {
	
	/**
	 * 将当前联系人信息同步到数据库
	 */
	public void syncFriend(WxAccount wxAccount, JSONObject json) {
		long seq = json.getLongValue(WxConst.KEY_SEQ);
		WxFriend wxFriend = rf.getWxFriendRepo().findByWxAccountAndSeq(wxAccount, seq);
		if (wxFriend == null) {
			wxFriend = new WxFriend();
			wxFriend.setWxAccount(wxAccount);
			wxFriend.setSeq(seq);
		}
		wxFriend.setNickName(json.getString(WxConst.KEY_NICKNAME));
		wxFriend.setRemarkName(json.getString(WxConst.KEY_REMARKNAME));
		rf.getWxFriendRepo().save(wxFriend);
	}
	
	/**
	 * 将当前
	 */
	public void syncGroup(WxAccount wxAccount, JSONObject json) {
		long seq = json.getLongValue(WxConst.KEY_SEQ);
		WxGroup wxGroup = rf.getWxGroupRepo().findByWxAccountAndSeq(wxAccount, seq);
		if (wxGroup == null) {
			wxGroup = new WxGroup();
			wxGroup.setWxAccount(wxAccount);
			wxGroup.setSeq(seq);
		}
		wxGroup.setNickName(json.getString(WxConst.KEY_NICKNAME));
		wxGroup.setRemarkName(json.getString(WxConst.KEY_REMARKNAME));
		rf.getWxGroupRepo().save(wxGroup);
	}
}




