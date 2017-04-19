package com.abc.test.service;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.abc.test.domain.WxAccount;
import com.abc.test.wx.WxMeta;

@Service
public class WxPersistenceService {

	public WxAccount saveWxAccount(WxMeta meta) {
		String uin = meta.getUser().getString("Uin");
		WxAccount wxAccount = f.getWxAccountRepo().findByUin(uin);
		if (wxAccount == null) {
			wxAccount = new WxAccount();
			wxAccount.setUin(uin);
			wxAccount.setOwner(meta.getOwner());
			wxAccount.setNickName(meta.getUser().getString("NickName"));
		}
		wxAccount.setLastLoginDate(new Date());
		return f.getWxAccountRepo().save(wxAccount);
	}
	
	
}











