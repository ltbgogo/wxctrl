
var MsgBeauty = {};

MsgBeauty.beautify = function(msg) {
	if (msg.msgType === 49) {
		
	}
};

.var s = "&lt;?xml version=\"1.0\"?&gt;<br/>&lt;msg&gt;<br/> &lt;appmsg appid=\"\" sdkver=\"0\"&gt;<br/> &lt;title&gt;【京东超市】卫诺 香氛洁厕液（清怡罗兰）500g&lt;/title&gt;<br/> &lt;des&gt;售价：¥1.00。京东商城，正品保证。&lt;/des&gt;<br/> &lt;action /&gt;<br/> &lt;type&gt;5&lt;/type&gt;<br/> &lt;showtype&gt;0&lt;/showtype&gt;<br/> &lt;soundtype&gt;0&lt;/soundtype&gt;<br/> &lt;mediatagname /&gt;<br/> &lt;messageext /&gt;<br/> &lt;messageaction /&gt;<br/> &lt;content /&gt;<br/> &lt;contentattr&gt;0&lt;/contentattr&gt;<br/> &lt;url&gt;https://union-click.jd.com/jdc?d=7Zvxc4&amp;amp;come=appmessage&lt;/url&gt;<br/> &lt;lowurl /&gt;<br/> &lt;dataurl /&gt;<br/> &lt;lowdataurl /&gt;<br/> &lt;appattach&gt;<br/> &lt;totallen&gt;0&lt;/totallen&gt;<br/> &lt;attachid /&gt;<br/> &lt;emoticonmd5 /&gt;<br/> &lt;fileext /&gt;<br/> &lt;cdnthumburl&gt;304b020100044430420201000204241504ef02032f55fb02042467197002045947c0910420777869645f62356869353678766b316832313237355f313439373837343537360201000201000400&lt;/cdnthumburl&gt;<br/> &lt;cdnthumbmd5&gt;749c3e08d1a630107ade4dc1e3a067b3&lt;/cdnthumbmd5&gt;<br/> &lt;cdnthumblength&gt;3161&lt;/cdnthumblength&gt;<br/> &lt;cdnthumbwidth&gt;160&lt;/cdnthumbwidth&gt;<br/> &lt;cdnthumbheight&gt;160&lt;/cdnthumbheight&gt;<br/> &lt;cdnthumbaeskey&gt;e13cf83c8e7546f388fbce6c713838db&lt;/cdnthumbaeskey&gt;<br/> &lt;aeskey&gt;e13cf83c8e7546f388fbce6c713838db&lt;/aeskey&gt;<br/> &lt;encryver&gt;0&lt;/encryver&gt;<br/> &lt;/appattach&gt;<br/> &lt;extinfo /&gt;<br/> &lt;sourceusername /&gt;<br/> &lt;sourcedisplayname /&gt;<br/> &lt;thumburl&gt;http://img13.360buyimg.com/n7/g13/M0A/11/1F/rBEhUlJk8NgIAAAAAAC0yGPOZIYAAEaDADQ1BUAALTg442.jpg&lt;/thumburl&gt;<br/> &lt;md5 /&gt;<br/> &lt;statextstr /&gt;<br/> &lt;/appmsg&gt;<br/> &lt;fromusername&gt;tjytjytjy0&lt;/fromusername&gt;<br/> &lt;scene&gt;0&lt;/scene&gt;<br/> &lt;appinfo&gt;<br/> &lt;version&gt;1&lt;/version&gt;<br/> &lt;appname&gt;&lt;/appname&gt;<br/> &lt;/appinfo&gt;<br/> &lt;commenturl&gt;&lt;/commenturl&gt;<br/>&lt;/msg&gt;<br/><br/>";
s = s.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/<br\/>/g, "");
console.log(s);
var appmsg = $($.parseXML(s)).find("msg appmsg");

console.log(appmsg.find("title").text());
console.log(appmsg.find("des").text());
console.log(appmsg.find("url").text());
console.log(appmsg.find("thumburl").text());