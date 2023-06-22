package edu.usst.jayce.server.service;

import org.springframework.web.multipart.*;
import javax.servlet.http.*;

public interface FileService {
	String checkUploadFile(final HttpServletRequest request, final HttpServletResponse response);

	String doUploadFile(final HttpServletRequest request, final HttpServletResponse response, final MultipartFile file);

	String deleteFile(final HttpServletRequest request);

	void doDownloadFile(final HttpServletRequest request, final HttpServletResponse response);

	String doRenameFile(final HttpServletRequest request);

	String deleteCheckedFiles(final HttpServletRequest request);

	String getPackTime(final HttpServletRequest request);

	String downloadCheckedFiles(final HttpServletRequest request);

	void downloadCheckedFilesZip(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
	
	// 移动文件的前置判断操作
	String confirmMoveFiles(final HttpServletRequest request);
	
	// 执行移动文件操作
	String doMoveFiles(final HttpServletRequest request);

	// 上传文件夹前置检查
	String checkImportFolder(final HttpServletRequest request);

	// 执行上传文件夹的操作
	String doImportFolder(final HttpServletRequest request, final MultipartFile file);
}
