package edu.usst.jayce.server.controller;

import edu.usst.jayce.server.service.*;
import org.springframework.stereotype.*;
import javax.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import javax.servlet.http.*;

// 主控制器
@Controller
@RequestMapping({ "/homeController" })
public class HomeController {
	private static final String CHARSET_BY_AJAX = "text/html; charset=utf-8";
	@Resource
	private ServerInfoService si;
	@Resource
	private AccountService as;
	@Resource
	private FolderViewService fvs;
	@Resource
	private FolderService fs;
	@Resource
	private FileService fis;
	@Resource
	private PlayVideoService pvs;
	@Resource
	private ShowPictureService sps;
	@Resource
	private PlayAudioService pas;
	@Resource
	private FileChainService fcs;

	@RequestMapping({ "/getServerOS.ajax" })
	@ResponseBody
	public String getServerOS() {
		return this.si.getOSName();
	}

	@RequestMapping(value = { "/getPublicKey.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getPublicKey() {
		return this.as.getPublicKey();
	}

	@RequestMapping({ "/doLogin.ajax" })
	@ResponseBody
	public String doLogin(final HttpServletRequest request, final HttpSession session) {
		return this.as.checkLoginRequest(request, session);
	}

	// 获取一个新验证码并存入请求者的Session中
	@RequestMapping({ "/getNewVerCode.do" })
	public void getNewVerCode(final HttpServletRequest request, final HttpServletResponse response,
			final HttpSession session) {
		as.getNewLoginVerCode(request, response, session);
	}

	// 修改密码
	@RequestMapping(value = { "/doChangePassword.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String doChangePassword(final HttpServletRequest request) {
		return as.changePassword(request);
	}

	@RequestMapping(value = { "/getFolderView.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getFolderView(final String fid, final HttpSession session, final HttpServletRequest request) {
		return fvs.getFolderViewToJson(fid, session, request);
	}
	
	@RequestMapping(value = { "/getRemainingFolderView.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getRemainingFolderView(final HttpServletRequest request) {
		return fvs.getRemainingFolderViewToJson(request);
	}

	@RequestMapping({ "/doLogout.ajax" })
	public @ResponseBody String doLogout(final HttpSession session) {
		this.as.logout(session);
		return "SUCCESS";
	}

	@RequestMapping({ "/newFolder.ajax" })
	@ResponseBody
	public String newFolder(final HttpServletRequest request) {
		return this.fs.newFolder(request);
	}

	@RequestMapping({ "/deleteFolder.ajax" })
	@ResponseBody
	public String deleteFolder(final HttpServletRequest request) {
		return this.fs.deleteFolder(request);
	}

	@RequestMapping({ "/renameFolder.ajax" })
	@ResponseBody
	public String renameFolder(final HttpServletRequest request) {
		return this.fs.renameFolder(request);
	}

	@RequestMapping(value = { "/douploadFile.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String douploadFile(final HttpServletRequest request, final HttpServletResponse response,
			final MultipartFile file) {
		return this.fis.doUploadFile(request, response, file);
	}

	@RequestMapping(value = { "/checkUploadFile.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String checkUploadFile(final HttpServletRequest request, final HttpServletResponse response) {
		return this.fis.checkUploadFile(request, response);
	}

	// 上传文件夹的前置检查流程
	@RequestMapping(value = { "/checkImportFolder.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String checkImportFolder(final HttpServletRequest request) {
		return this.fis.checkImportFolder(request);
	}

	// 执行文件夹上传操作
	@RequestMapping(value = { "/doImportFolder.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String doImportFolder(final HttpServletRequest request, final MultipartFile file) {
		return fis.doImportFolder(request, file);
	}

	// 上传文件夹时，若存在同名文件夹并选择覆盖，则应先执行该方法，执行成功后再上传新的文件夹
	@RequestMapping(value = { "/deleteFolderByName.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String deleteFolderByName(final HttpServletRequest request) {
		return fs.deleteFolderByName(request);
	}

	// 上传文件夹时，若存在同名文件夹并选择保留两者，则应先执行该方法，执行成功后使用返回的新文件夹名进行上传
	@RequestMapping(value = { "/createNewFolderByName.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String createNewFolderByName(final HttpServletRequest request) {
		return fs.createNewFolderByName(request);
	}

	@RequestMapping({ "/deleteFile.ajax" })
	@ResponseBody
	public String deleteFile(final HttpServletRequest request) {
		return this.fis.deleteFile(request);
	}

	@RequestMapping({ "/downloadFile.do" })
	public void downloadFile(final HttpServletRequest request, final HttpServletResponse response) {
		this.fis.doDownloadFile(request, response);
	}

	@RequestMapping({ "/renameFile.ajax" })
	@ResponseBody
	public String renameFile(final HttpServletRequest request) {
		return this.fis.doRenameFile(request);
	}

	@RequestMapping(value = { "/playVideo.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String playVideo(final HttpServletRequest request, final HttpServletResponse response) {
		return this.pvs.getPlayVideoJson(request);
	}

	// 预览图片请求
	@RequestMapping(value = { "/getPrePicture.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getPrePicture(final HttpServletRequest request) {
		return this.sps.getPreviewPictureJson(request);
	}

	// 获取压缩的预览图片
	@RequestMapping({ "/showCondensedPicture.do" })
	public void showCondensedPicture(final HttpServletRequest request, final HttpServletResponse response) {
		sps.getCondensedPicture(request, response);
	}

	@RequestMapping({ "/deleteCheckedFiles.ajax" })
	@ResponseBody
	public String deleteCheckedFiles(final HttpServletRequest request) {
		return this.fis.deleteCheckedFiles(request);
	}

	@RequestMapping({ "/getPackTime.ajax" })
	@ResponseBody
	public String getPackTime(final HttpServletRequest request) {
		return this.fis.getPackTime(request);
	}

	@RequestMapping({ "/downloadCheckedFiles.ajax" })
	@ResponseBody
	public String downloadCheckedFiles(final HttpServletRequest request) {
		return this.fis.downloadCheckedFiles(request);
	}

	@RequestMapping({ "/downloadCheckedFilesZip.do" })
	public void downloadCheckedFilesZip(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		this.fis.downloadCheckedFilesZip(request, response);
	}

	@RequestMapping(value = { "/playAudios.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String playAudios(final HttpServletRequest request) {
		return this.pas.getAudioInfoListByJson(request);
	}
	
	// 移动文件操作前置确认
	@RequestMapping(value = { "/confirmMoveFiles.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String confirmMoveFiles(final HttpServletRequest request) {
		return fis.confirmMoveFiles(request);
	}
	
	// 执行移动文件操作
	@RequestMapping({ "/moveCheckedFiles.ajax" })
	@ResponseBody
	public String moveCheckedFiles(final HttpServletRequest request) {
		return fis.doMoveFiles(request);
	}
	
	// 执行全局查询
	@RequestMapping(value = { "/sreachInCompletePath.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String sreachInCompletePath(final HttpServletRequest request) {
		return fvs.getSearchViewToJson(request);
	}

	//应答机制
	@RequestMapping(value = { "/ping.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String pong(final HttpServletRequest request) {
		return as.doPong(request);
	}

	// 询问是否开启自由注册新账户功能
	@RequestMapping(value = { "/askForAllowSignUpOrNot.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String askForAllowSignUpOrNot(final HttpServletRequest request) {
		return as.isAllowSignUp();
	}

	// 处理注册新账户请求
	@RequestMapping(value = { "/doSigUp.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String doSigUp(final HttpServletRequest request) {
		return as.doSignUp(request);
	}

	// 获取永久资源链接的对应ckey
	@RequestMapping(value = { "/getFileChainKey.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getFileChainKey(final HttpServletRequest request) {
		return fcs.getChainKeyByFid(request);
	}
}
