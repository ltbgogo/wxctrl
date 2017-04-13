package com.abc.test.service;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.abc.test.domain.WxAccount;
import com.abc.test.wx.WxMeta;

@Service
public class WxPersistenceService {

	public WxAccount saveWxAccount(WxMeta meta) {
		WxAccount wxAccount = f.getWxAccountRepo().findByUin(meta.getUser().getIntValue("Uin"));
		if (wxAccount == null) {
			wxAccount = new WxAccount();
			wxAccount.setUin(meta.getUser().getIntValue("Uin"));
			wxAccount.setOwner(meta.getOwner());
			wxAccount.setNickName(meta.getUser().getString("NickName"));
		}
		wxAccount.setLastLoginDate(new Date());
		return f.getWxAccountRepo().save(wxAccount);
	}
	
}
