package edu.usst.jayce.server.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//外部下载链接处理服务
public interface ExternalDownloadService {
	
	// 获取一个下载凭证
	String getDownloadKey(HttpServletRequest request);
	
	// 使用凭证下载指定文件
	void downloadFileByKey(HttpServletRequest request,HttpServletResponse response);

}
