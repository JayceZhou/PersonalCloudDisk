package edu.usst.jayce.server.service;

import javax.servlet.http.*;

public interface FolderService
{
    String newFolder(final HttpServletRequest request);
    
    String deleteFolder(final HttpServletRequest request);
    
    String renameFolder(final HttpServletRequest request);
    
    String deleteFolderByName(final HttpServletRequest request);
    
    // 上传文件夹前置操作：创建一个新名称的文件夹
    String createNewFolderByName(final HttpServletRequest request);
}
