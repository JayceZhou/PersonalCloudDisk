package edu.usst.jayce.server.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//外部链接处理器
public interface FileChainService {
	
	//根据文件ID获取其永久资源链接的ckey
	public String getChainKeyByFid(HttpServletRequest request);
	
	// 根据链接中的ckey返回对应的资源数据
	public void getResourceByChainKey(HttpServletRequest request,HttpServletResponse response);

}
