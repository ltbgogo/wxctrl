package com.abc.wxctrl.wx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.jpa.event.internal.core.JpaAutoFlushEventListener;
import org.xml.sax.InputSource;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import static com.abc.wxctrl.repository.RepoFactory.rf;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.manager.SpringManager;
import com.abc.wxctrl.repository.WxAccountRepository;
import com.abc.wxctrl.utility.TxExecutor;
import com.abc.wxctrl.utility.spec.Processor;
import com.alibaba.fastjson.JSONObject;

@Log4j
public class WxMetaStorage {

	/**
	 * 键：微信的编号
	 * 值：微信的元数据信息
	 */
	private static Map<String, WxMeta> data = Collections.synchronizedMap(new HashMap<String, WxMeta>());
	
	/**
	 * 根据微信编号，获取微信元数据信息
	 */
	public static WxMeta get(String wxAccountId) {
		return data.get(wxAccountId);
	}
	
	public static WxMeta get(WxAccount wxAccount) {
		return data.get(wxAccount.getId());
	}
	
	/**
	 * 存储微信元数据信息，将信息缓存到本地
	 */
	@SneakyThrows
	public static void put(WxMeta meta) {
		data.put(meta.getWxAccount().getId(), meta);
		//缓存数据
		File cacheFile = getCacheFile(meta.getWxAccount().getId());
		cacheFile.getParentFile().mkdirs();
		@Cleanup
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
		oos.writeObject(meta);
	}
	
	/**
	 * 此方法应用于服务器重启之后，从磁盘缓存还原元数据信息
	 */
	public static void restoreFromDiskCache() {
		TxExecutor.process(new Processor() {
			@Override
			public void process() throws Exception {
				for (User user : rf.getUserRepo().findAll()) {
					for (WxAccount account : user.getWxAccounts()) {
						File cacheFile = getCacheFile(account.getId());
						if (cacheFile.exists()) {
							try { 
								@Cleanup
								ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
								WxMeta meta = (WxMeta) ois.readObject();
								JSONObject syncStatus = meta.getHttpClient().syncCheck();	
								if (!Arrays.asList(1102, 1101).contains(syncStatus.getIntValue("retcode"))) {
									meta.getMsgListener().start();
									continue;
								}
							} catch (Exception e) {
								cacheFile.delete();
								log.error(e.getMessage(), e);
							}
						}
						rf.getWxAccountRepo().save(account);
					}
				}
			}
		});
	}
	
	/**
	 * 返回缓存文件路径
	 */
	private static File getCacheFile(String wxAccountId) {
		return FileUtils.getFile(WxConst.TMP_DIR, "wxmeta", wxAccountId);
	}
}




