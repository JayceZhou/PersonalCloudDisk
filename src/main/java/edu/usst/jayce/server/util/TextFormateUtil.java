package edu.usst.jayce.server.util;

import java.util.regex.*;

public class TextFormateUtil
{
    private static TextFormateUtil tfu;
    
    public static TextFormateUtil instance() {
        return TextFormateUtil.tfu;
    }
    
    public boolean matcherFolderName(final String folderName) {
    		final Pattern p = Pattern.compile("[|\\/*<>\"?&$:]+");
        final Matcher m = p.matcher(folderName);
        return !m.find();
    }
    
    public boolean matcherFileName(final String fileName) {
        final Pattern p = Pattern.compile("[|\\/*<>\"?&$:]+");
        final Matcher m = p.matcher(fileName);
        return !m.find();
    }
    
    // 判断字符串中是否含有转义符
	public boolean hasEscapes(String in) {
		return in.indexOf("\\") >= 0;
	}
    
    static {
        TextFormateUtil.tfu = new TextFormateUtil();
    }
}
