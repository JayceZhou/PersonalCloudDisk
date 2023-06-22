package edu.usst.jayce.server.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResourceService {

	// 获取无需格式转化的流资源
	public void getResource(String fid, HttpServletRequest request, HttpServletResponse response);

	// 获取Word资源，以PDF流格式写回
	public void getWordView(String fileId, HttpServletRequest request, HttpServletResponse response);

	// 获取TXT资源，以PDF流格式写回
	public void getTxtView(String fileId, HttpServletRequest request, HttpServletResponse response);

	//获取PPT资源，以PDF流格式写回
	public void getPPTView(String fileId, HttpServletRequest request, HttpServletResponse response);

	// 获取视频解码状态
	public String getVideoTranscodeStatus(HttpServletRequest request);

	// 获取LRC文本内容
	public void getLRContextByUTF8(String fileId, HttpServletRequest request, HttpServletResponse response);

	// 获取公告信息的MD5
	public String getNoticeMD5();

	// 获取公告信息的HTML内容
	public void getNoticeContext(HttpServletRequest request, HttpServletResponse response);

}
