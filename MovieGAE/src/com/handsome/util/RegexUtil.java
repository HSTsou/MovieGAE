package com.handsome.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	String patternStr;

	public RegexUtil(String patternStr) {
		this.patternStr = patternStr;
	}

	//if find pattern return index, or -1.
	public int getIndexOfStr(String Str) {
		
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(Str);
		
		if (matcher.find()) {
			//System.out.println(matcher.start());// this will give you index
			return matcher.start();
		}else{
			return -1;
		}
	}

}
