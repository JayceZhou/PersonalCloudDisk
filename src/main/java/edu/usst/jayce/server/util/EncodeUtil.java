package edu.usst.jayce.server.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// 字符串编码工具
public class EncodeUtil {

	private EncodeUtil() {
	}

	// 文件名转码
	public static String getFileNameByUTF8(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}

}
