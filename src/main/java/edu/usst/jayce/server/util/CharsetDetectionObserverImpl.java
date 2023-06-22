package edu.usst.jayce.server.util;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

// 判断文本编码格式回调封装类
public class CharsetDetectionObserverImpl implements nsICharsetDetectionObserver{
	
	private String charset;

	@Override
	public void Notify(String arg0) {
		// TODO 自动生成的方法存根
		charset=arg0;
	}

	public String getCharset() {
		return charset;
	}
	
}
