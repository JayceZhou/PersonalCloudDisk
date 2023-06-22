package edu.usst.jayce.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import edu.usst.jayce.printer.Printer;

// 简述
@Component
public class NoticeUtil {

	private String md5;// 公告信息的md5值，如未生成则返回null
	private MutableDataHolder options;// markdown解析器的参数设置
	// 程序主目录中的公告文件名称
	public static final String NOTICE_FILE_NAME = "notice.md";

	public static final String NOTICE_OUTPUT_NAME = "notice.html";

	@Resource
	private LogUtil lu;
	@Resource
	private TxtCharsetGetter tcg;

	public NoticeUtil() {
		options = new MutableDataSet();
		options.setFrom(ParserEmulationProfile.MARKDOWN);
	}

	// 载入公告文件
	public void loadNotice() {
		File noticeMD = new File(ConfigureReader.instance().getPath(), NOTICE_FILE_NAME);
		File noticeHTML = new File(ConfigureReader.instance().getTemporaryfilePath(), NOTICE_OUTPUT_NAME);// 转化后的输出位置
		if (noticeMD.isFile() && noticeMD.canRead()) {
			Printer.instance.print("正在载入公告信息...");
			try {
				// 先判断公告信息文件的编码格式
				String inputFileEncode = tcg.getTxtCharset(new FileInputStream(noticeMD));
				// 将其转化为HTML格式并保存
				Parser parser = Parser.builder(options).build();
				HtmlRenderer renderer = HtmlRenderer.builder(options).build();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(noticeMD), inputFileEncode));
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(noticeHTML), "UTF-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String html = renderer.render(parser.parse(line));
					writer.write(html);
					writer.newLine();
				}
				reader.close();
				writer.flush();
				writer.close();
				// 计算md5并保存
				md5 = DigestUtils.md5Hex(new FileInputStream(noticeMD));
				Printer.instance.print("公告信息载入完成。");
				return;
			} catch (Exception e) {
				Printer.instance.print("错误：公告文件载入失败，服务器将无法为用户显示公告内容。");
			}
		}
		md5 = null;
	}

	// 获取公告信息的md5标识
	public String getMd5() {
		return md5;
	}
}
