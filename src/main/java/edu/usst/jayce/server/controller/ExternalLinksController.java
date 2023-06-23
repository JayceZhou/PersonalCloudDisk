package edu.usst.jayce.server.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.usst.jayce.server.service.ExternalDownloadService;
import edu.usst.jayce.server.service.FileChainService;

// 外部链接控制器
@Controller
@CrossOrigin
@RequestMapping({"/externalLinksController"})
public class ExternalLinksController {
	
	@Resource
	private ExternalDownloadService eds;//分享下载链接的相关处理
	@Resource
	private FileChainService fcs;
	
	@RequestMapping("/getDownloadKey.ajax")
	public @ResponseBody String getDownloadKey(HttpServletRequest request) {
		return eds.getDownloadKey(request);
	}
	
	@RequestMapping("/downloadFileByKey/{fileName}")
	public void downloadFileByKey(HttpServletRequest request,HttpServletResponse response) {
		eds.downloadFileByKey(request, response);
	}
	
	@RequestMapping("/chain/{fileName}")
	public void chain(HttpServletRequest request,HttpServletResponse response) {
		fcs.getResourceByChainKey(request, response);
	}

}
