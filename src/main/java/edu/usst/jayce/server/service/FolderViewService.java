package edu.usst.jayce.server.service;

import javax.servlet.http.*;

// 文件夹视图逻辑处理
public interface FolderViewService{
	
	//根据主键获取文件夹视图
    String getFolderViewToJson(final String fid, final HttpSession session, final HttpServletRequest request);
    
    // 全路径查询
    String getSearchViewToJson(final HttpServletRequest request);

    // 根据主键获取文件夹的后续视图
	String getRemainingFolderViewToJson(HttpServletRequest request);
}
