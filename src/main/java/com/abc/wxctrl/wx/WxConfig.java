package com.abc.wxctrl.wx;

import lombok.Data;

@Data
public class WxConfig {

	private boolean debug = false;
	private boolean autoReplyMode = false;
//    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36";
    private boolean interactive = false;
    private boolean autoOpen = false;
}
