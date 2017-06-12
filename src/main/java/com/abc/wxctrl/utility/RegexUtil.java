package com.abc.wxctrl.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import com.abc.wxctrl.utility.spec.InOutProcessor;

public class RegexUtil {
	
	public static Matcher getMatcher(String string, String regex) {
		return Pattern.compile(regex).matcher(string);
	}
	
	/**
	 * System.out.println(RegexUtil.matchGroups("aabbaabb", "(a)(b)")); //[[a, b], [a, b]]
	 */
	public static List<List<String>> matchGroups(String string, String regex) {
		List<List<String>> groups = new ArrayList<List<String>>();
        Matcher m = getMatcher(string, regex);
        while (m.find()) {
        	List<String> group = new ArrayList<String>();
        	for (int i = 1; i <= m.groupCount(); i++) {
        		group.add(m.group(i));
        	}
        	groups.add(group);
        }
        return groups;
	}
	
	public static String matchFirstGroup(String string, String regex) {
		List<List<String>> groups = matchGroups(string, regex);
		return groups.isEmpty() ? null : groups.get(0).get(0);
	}
	
	public static boolean isMatch(String string, String regex) {
		return string == null ? false : string.matches(regex);
    }
	
    public static List<String> match(String string, String regex) {
        List<String> results = new ArrayList<String>();
        Matcher m = getMatcher(string, regex);
        while (m.find()) {
            results.add(m.group());
        }
        return results;
    }
    
    public static boolean isMatch(String string, String[] inclusions, String[] exclusions) {
    	inclusions = ArrayUtils.isEmpty(inclusions) ? new String[] {"[\\s\\S]*"} : inclusions;
		for (String inclusion : inclusions) {
			if (string.matches(inclusion)) {
				for (String exclusion : exclusions) {
					if (string.matches(exclusion)) { return false; }
				}
				return true;
			}
		}
		return false;
	}

    public static String replace(String string, String regex, InOutProcessor<String, String> processor) {
    	try {
	    	StringBuffer sb = new StringBuffer();
			Matcher m = getMatcher(string, regex);
			while (m.find()) {
				m.appendReplacement(sb, Matcher.quoteReplacement(processor.process(m.group())));
			}
			m.appendTail(sb);
			return sb.toString();
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();
	public static boolean matchAnyPath(String path, String...patterns) {
		for (String pattern : patterns) {
			if (antPathMatcher.match(pattern, path)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		String regex = "<s:message .+?/>";
		File dir = new File("E:\\workspace\\eclipse_epai\\073commonComponent\\src\\main\\webapp\\CommonComponent\\DocumentMgr");
		for (File file : FileUtils.listFiles(dir, new String[] {"jsp"}, true)) {
			String s = FileUtils.readFileToString(file, "UTF-8");
			RegexUtil.replace(s, regex, new InOutProcessor<String, String>() {
				public String process(String in) throws Exception {
					in = in.replace("\"", "'");
					String key = StringUtils.substringBetween(in, "code='", "'");
					String text = StringUtils.substringBetween(in, "text='", "'");
					in = String.format("<i18n ikey=%s>%s</i18n>", key.replaceFirst("fs", "SYS_FS").replace(".", "_"), text);
					return in;
				}
			});
			FileUtils.writeStringToFile(file, s, "UTF-8");
		}
	}
}










