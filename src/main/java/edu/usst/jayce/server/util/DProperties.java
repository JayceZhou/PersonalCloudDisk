package edu.usst.jayce.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Properties类
public class DProperties {

	private List<LineContext> contexts = new ArrayList<>();// 保存载入的整个文本信息
	private Map<String, String> properties = new HashMap<>();// 仅保存配置信息，用于提高查询效率

	// 用于存储每一行文本信息的包装类
	private class LineContext {
		private String key;// 键
		private String value;// 值
		private String text;// 文本

		private LineContext(String key, String value, String text) {
			this.key = key;
			this.value = value;
			this.text = text;
		}

		@Override
		public boolean equals(Object obj) {
			if (key == null) {
				return false;
			}
			return key.equals(obj);
		}
	}

	// 获取参数
	public String getProperty(String key) {
		if (key != null) {
			return properties.get(key);
		}
		return null;// 否则返回null
	}

	// 获取参数
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return value == null ? defaultValue : value;
	}

	// 新增一个配置或修改已有的配置
	public void setProperty(String key, String value) {
		if (key != null) {
			properties.put(key, value);
			for (LineContext lc : contexts) {
				if (key.equals(lc.key)) {
					lc.value = value;
					return;
				}
			}
			contexts.add(new LineContext(key, value, null));
		}
	}

	// 从文本文件中载入配置项
	public void load(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "8859_1"));
		String lineStr = null;
		// 按行读取文本
		clear();
		while ((lineStr = reader.readLine()) != null && contexts.size() < Integer.MAX_VALUE) {
			if (lineStr.startsWith("#")) {
				contexts.add(new LineContext(null, null, lineStr));// 保存为注释
			} else {
				int delimit0 = lineStr.indexOf("=");
				int delimit1 = lineStr.indexOf(":");// 兼容Properties的“:”分割规则，但保存时将统一改为“=”
				int delimitIndex = -1;// 判断第一个出现的分隔符的位置
				if (delimit0 >= 0) {
					delimitIndex = delimit0;
				}
				if (delimit1 >= 0 && delimit1 < delimit0) {
					delimitIndex = delimit1;
				}
				if (delimitIndex >= 0) {
					setProperty(lineStr.substring(0, delimitIndex), lineStr.substring(delimitIndex + 1));// 保存为键值对
				} else {
					contexts.add(new LineContext(null, null, lineStr));// 保存为其他文本
				}
			}
		}
		reader.close();
	}

	// 覆盖并保存配置
	public void store(OutputStream out, String header) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
		if (header != null) {
			writer.write("#" + header);
			writer.newLine();
			writer.write("#" + new Date().toString());
			writer.newLine();
		}
		for (LineContext line : contexts) {
			if (line.key != null) {
				writer.write(line.key + "=" + line.value);
				writer.newLine();
			} else {
				writer.write(line.text);
				writer.newLine();
			}
		}
		writer.close();
	}

	// 获得所有配置项
	public Set<String> stringPropertieNames() {
		return properties.keySet();
	}

	// 清除某项配置
	public void removeProperty(String key) {
		if (key != null) {
			properties.remove(key);
			for(Iterator<LineContext> itor=contexts.iterator();itor.hasNext();) {
				if(key.equals(itor.next().key)) {
					itor.remove();
				}
			}
		}
	}

	// 清空所有配置项
	public void clear() {
		contexts.clear();
		properties.clear();
	}

}
