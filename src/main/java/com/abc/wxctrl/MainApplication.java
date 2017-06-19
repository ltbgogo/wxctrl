package com.abc.wxctrl;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.w3c.dom.Document;

import com.abc.wxctrl.WebApplication;
import com.abc.wxctrl.config.AppConfigBean;
import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.manager.SpringManager;
import com.abc.wxctrl.manager.UserManager;
import com.abc.wxctrl.repository.RepoFactory;
import com.abc.wxctrl.repository.WxAccountRepository;
import com.abc.wxctrl.utility.FileUtil;
import com.abc.wxctrl.utility.IOUtil;
import com.abc.wxctrl.utility.JsonUtil;
import com.abc.wxctrl.utility.RegexUtil;
import com.abc.wxctrl.utility.XmlUtil;
import com.abc.wxctrl.wx.WxApp;
import com.abc.wxctrl.wx.WxMeta;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Service
@Log4j
public class MainApplication {
	
	@SneakyThrows
	public static void main(String[] args) {
//		SpringManager.startMailApplication(WebApplication.class, args);
//		MainApplication app = SpringManager.getBean(MainApplication.class);
//		app.run();
//		String s = "filehelper,@@2f4c5feb681885c249847ebc0ab7f68eef677be951c62634790f48563d44f9f5,@@cd852d50f89b79ef65d5e5f84730976d9c417117d76b93510e6c4ba33ca0c026,@6c614b8aaf362cc9740cbaffdfd7f6b69c06c8ab278414f92c4fdd5e5c15d4a5,@13e0cf6529dbc765c36f6c69c3a16c1c,@566fd13501c34a2933acf7ca79a76076,@6fca24dd9708aebbb0ad8c7df26cfcd7,@@be39e2c82fa6bd94ea5b7eeef5a22e597cc89e29d6bedf0fa6ce4470865c4607,@6db601410b0c0936ea8078bec5106659,@14f3dd688bcd33adfe4b19d512cb0684,@7456f89bbe1740545b7309de4186d69a,@9a2c959c53e99267effb5f36d4c3c696,@5e3bcfdbd058c863dfa92d7a01e9c333,@077dff1593ec8dbfe36f73205fe0288b,@ebdddabf6c77e767cc428208d2c1f89b,@7ffe029daa97ea50892e50792a9a2b2ca7ed4731a690584fa4b4399af095c7d9,@85f3bc4fa1b0168ee17a7fa10a9767c2,qqmail,@38ca10872495b5ebb8b4d8bcb6000f62,@2f00e57d16f0c79fc9774c4ba340f16a,@6a8ab430d71cacce305d072625344a05,@d7486c8558b1f3aa93d5fe1dad774533ab5358a7ed405f0b823144a0f8e80027,@912ac1e76adb45579bf40e39e8d62a79,@a4405a5c0235f72accbcfc144cb8a446,@@2a0c9751f9a04ce73acfced700fc1cdcbbeadf312ad9debe5f1a004523b29f4d,@686ec1366ad253cda734b199950bdd6d4ecb5dc877fe29e065ca39b91534bc9e,@@1fea223c2a33675400eddc0a73545c2dc28568c3b3b4d2f9127d6bddc26b69ee,@362bf8146fafa47ecf4e5eb5298676aa,@@48765beaa1485006bbddfa29b86c0dd7820dc991c93c7ab58e2336b67cb279f3,@@bcafe61efc3fdc00862d4b15b4f20d552bb9eb9fb44f331df8d503a516ad7329,@c1402d89981bc64ee9e6a8d98f110ada,@30170f06eeda0e44fcf1f2391091119f,@c43aca5930eee82d09e9814a1b75bad6,@b4d960fc690d045320093a5d06ed4044,@41b96de7ee89ba428267c4c7eeff489c,@@ffc26b85d4fd36b68a5b8e2ed353b0901780474a72dfe10f6c6fa060642b2736,@9d62d09752814f2c23b9925804e6c9980c3f5a74210f53a9a1d9fd2fea9e0fe1,@af7094d4f7e4323ca64340b31b0b33365d6189b863f375f5f1b6fe2f70bd4c38,@f5722a8c245689272ab3b162fcb5a2462f5d2a20184008f51a2f2e9f5c7026dd,@0acfa0ac2466e6cbfdcdffe0aa3d60388bbd208e0437cbb58f28f3a3eed6efa1,@d91f9ea472027bbb5b2e0ad5c1ce53f6,@9c7c0682e22efb6b58c821b7fdc1a8157bc6aba6ef526b78b7d68a9707ffe0a5,@2958616de7ae693710e02513d2bd670e,@8f3b11030909956f7949307246b4f0fc1a51ec45859e43f00e26d0ac3c72da64,@3d78c885b92db8cb10c04d9381089827,@e47cb10f39a51ce0a30233ae547c80ac,@6129dd4f7348d9f9a7bedba4fbae60d611256016b8f673afc5e47390d57bcbc5,@fde48335ab77545e5a3e3e15f7c8a9e8,@e18e550389b1c75d0148f5c10654029155af8c5f46f76a89795bb4518894ed26,@130459915d2e1ac885b76d360677f455,@0c7153fdf7139520626a76edcdaa7d03,@3d083d5f77900666957cea7820195167,@a48c836327d87d959cf5fc324b8bf353,weixin,@7e2484f9cd2a0a6a13d8d2016066f24c,@13535c3210ebf62eccf1da7c8593641c,@ec1615d812243351c658ebd0df1b7f17,@@7a121c0db90dd75c2f4ec51592035b537bf1e813137defd686dced3c9b042c48,@aaac36af5aab9f27f9ef7310760b706e9471fdde5b5d6f37ac82f932c81ce5a2,@84d823125ce6f5a5dcc403e5498b55bb,@0c88302cb77b99c8326f3303b07d1e00,@b71f0b418d8738d4a934ea246ad08efeb97b05f427060e4e98557a764a09b830,@771391c93d47d042de8409340df99abe,@70bc8214d19eaeaa0ecb0527ae33412b81ac77c0adf82ec933c70a26dc062872,@3949953003a0502e1c8a2783b60bf1702ef92efdecda73f5815eabe54dc53a26,@@b1af41c61e6cd910bdc45c61674fd99662987489289f2348b7a3eb41b2880fbf,@eaecf81792d3166a9d5acea588831a1b,@8749543eda5c20fd1460050f2650a220,@436881852d1780bc85f7326dc7b483438c63c4fba698a4dfc19a5b2417b520c9,@366747a240638fdfe048d01068328d83951c933b3b873746011ad205e92668e3,@ec62f97f9841e6a0193e50a39d66967f99f7d25e23852604e2face12758fd720,@5a5ab4e858c6dc133feb0fe3c923332caf9f5c0846242bf49811de8cfcee0acb,@bb55eaa7385ea3efd0c28ae37ce54930,@e50ff3f181d437a35fca920d9d3d86d0,@076498dcc2b444cc4bbcb564b2d8b2446786bc650a3ab1469079d9ba2d1f2b2c,@6dc1063f386e5bb07135b9eeb01bcdd06fb671ac79d37ffa7da1cd54ca0da04a,@3d6a78593d99d70e2b20865819081ba34a584eed6f27ec831bb7931479d8c90b,@@8bc84c59f41835278e5537ba2e1d4415ea09b8a91b4abd6549cc5199ddc6c69f,@26394db546fd3862c17807982369239a89007b0a562509dc26cdb147c7370a23,@e90299e5e41117246db40d48cc7f460d,@2ac64660114e8fcb3aa98054ec9bc262,@3d9d00b8086eb36903b88f02ce4063efe0dcb8d8b8d28eb918fdc4ffbdd0dc5c,@902c2fc957180d2b9db6ed41234cd2f5ff4e611c5ef6a4c3d7f64d8d053cfdf9,@b51cd7d1709b19ce728c22e05d255ab9,@61d6592858524f71548148f1a96b57b7,@f80c9751eb950f6de660127b978d708c,@2bb50ee3cdd107347745f32e5501bf5b8066db217cc51a8f927af46c063bd70e,@8728b7ebecdba8d1dc90eb7d7708465f,@2ff90ed05ad6f25a7aed5a293cc4507ecba73ab6682a8ec5610e5c8e43aa5bda,@@3b9df54126b6a73c176ac544b36512d1f2d593af374fcf329b0186b8dec8cb28,@a7f85d2f975712a47cabc241ea4ee91d,@1a39b6a9da695dc034760633c2ca5fce,@07a56440332f5e780255a7da14b76dcb,@20c1ec43a2e5c7bfde7e29d6d4dc4f1b2bc151d7742a71c75d9a2e6342becb69,@ac656eb49504eea4ac9822afaaebbb8f820f150d7ad434314f2d8477b3340e15,@0e9fdf4e1c8f8f9a886d0ea56fb59856,@8e0fd549454701ede35d69f06d25da0d,@3be6d0d4a840b3bef84bb6fc1b2af393bdf1b35c5bdf2f1b458bfba969d95cdf,@d5b3a14d8a290e1690d134dce5940ca804821d78541433d123cc58d737904d37,@80c6a1acb5e99791b0e3301960aeacadae819e7ce6a20f05763fd6b305aad421,@205270ed6094cc46a2f76eaef2507a063305736aee6e664e6f632a92f893b1ff,@001636c01c633faad42b04be06d39457f6bd080b6dde30f0a44cc3baf5ae74a8,@@95cc1f903032f8d66fdeeb604fe4c405";
//		for (String ss : RegexUtil.match(s, "@@[^,]+")) {
//			System.out.println(ss);
//		}
//		WxAccount account = SpringManager.getBean(WxAccountRepository.class).findOne("cow2zwihbslw627qljpns9i0");
//		RepoFactory.rf.getWxMsgRepo().findFriendMsgs(account, "xx", new PageRequest(0, 2));
		
		String s = "&lt;?xml version=\"1.0\"?&gt;<br/>&lt;msg&gt;<br/> &lt;appmsg appid=\"\" sdkver=\"0\"&gt;<br/> &lt;title&gt;【京东超市】卫诺 香氛洁厕液（清怡罗兰）500g&lt;/title&gt;<br/> &lt;des&gt;售价：¥1.00。京东商城，正品保证。&lt;/des&gt;<br/> &lt;action /&gt;<br/> &lt;type&gt;5&lt;/type&gt;<br/> &lt;showtype&gt;0&lt;/showtype&gt;<br/> &lt;soundtype&gt;0&lt;/soundtype&gt;<br/> &lt;mediatagname /&gt;<br/> &lt;messageext /&gt;<br/> &lt;messageaction /&gt;<br/> &lt;content /&gt;<br/> &lt;contentattr&gt;0&lt;/contentattr&gt;<br/> &lt;url&gt;https://union-click.jd.com/jdc?d=7Zvxc4&amp;amp;come=appmessage&lt;/url&gt;<br/> &lt;lowurl /&gt;<br/> &lt;dataurl /&gt;<br/> &lt;lowdataurl /&gt;<br/> &lt;appattach&gt;<br/> &lt;totallen&gt;0&lt;/totallen&gt;<br/> &lt;attachid /&gt;<br/> &lt;emoticonmd5 /&gt;<br/> &lt;fileext /&gt;<br/> &lt;cdnthumburl&gt;304b020100044430420201000204241504ef02032f55fb02042467197002045947c0910420777869645f62356869353678766b316832313237355f313439373837343537360201000201000400&lt;/cdnthumburl&gt;<br/> &lt;cdnthumbmd5&gt;749c3e08d1a630107ade4dc1e3a067b3&lt;/cdnthumbmd5&gt;<br/> &lt;cdnthumblength&gt;3161&lt;/cdnthumblength&gt;<br/> &lt;cdnthumbwidth&gt;160&lt;/cdnthumbwidth&gt;<br/> &lt;cdnthumbheight&gt;160&lt;/cdnthumbheight&gt;<br/> &lt;cdnthumbaeskey&gt;e13cf83c8e7546f388fbce6c713838db&lt;/cdnthumbaeskey&gt;<br/> &lt;aeskey&gt;e13cf83c8e7546f388fbce6c713838db&lt;/aeskey&gt;<br/> &lt;encryver&gt;0&lt;/encryver&gt;<br/> &lt;/appattach&gt;<br/> &lt;extinfo /&gt;<br/> &lt;sourceusername /&gt;<br/> &lt;sourcedisplayname /&gt;<br/> &lt;thumburl&gt;http://img13.360buyimg.com/n7/g13/M0A/11/1F/rBEhUlJk8NgIAAAAAAC0yGPOZIYAAEaDADQ1BUAALTg442.jpg&lt;/thumburl&gt;<br/> &lt;md5 /&gt;<br/> &lt;statextstr /&gt;<br/> &lt;/appmsg&gt;<br/> &lt;fromusername&gt;tjytjytjy0&lt;/fromusername&gt;<br/> &lt;scene&gt;0&lt;/scene&gt;<br/> &lt;appinfo&gt;<br/> &lt;version&gt;1&lt;/version&gt;<br/> &lt;appname&gt;&lt;/appname&gt;<br/> &lt;/appinfo&gt;<br/> &lt;commenturl&gt;&lt;/commenturl&gt;<br/>&lt;/msg&gt;<br/><br/>";
		s = s.replace("&lt;", "<").replace("&gt;", ">").replace("<br/>", "");
		System.out.println(s);
		Document doc = XmlUtil.XU.getDocument(s);
		
		System.out.println(XmlUtil.XU.getString("/msg/appmsg/title", doc));
		System.out.println(XmlUtil.XU.getString("/msg/appmsg/des", doc));
		System.out.println(XmlUtil.XU.getString("/msg/appmsg/url", doc));
		System.out.println(XmlUtil.XU.getString("/msg/appmsg/thumburl", doc));
		
		System.out.println(XmlUtil.XU.getString("/msg/emoji/@md5", doc));
		System.out.println(XmlUtil.XU.getString("/msg/emoji/@cdnurl", doc));
	}
	
	@SneakyThrows
	public void run() {
		WxApp app = SpringManager.getBean(WxApp.class);
		WxMeta meta = app.startOne();
		Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + meta.getQrCodeImg()});
	}
}




