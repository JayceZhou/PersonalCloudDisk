package edu.usst.jayce.server.pojo;

import java.util.List;

// 上传文件检查结果封装
public class CheckUploadFilesRespons {
	
	private String checkResult;//检查结果
	private List<String> pereFileNameList;//重复列表
	private String overSizeFile;//超限文件
	private String maxUploadFileSize;//最大上传体积
	
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}
	public List<String> getPereFileNameList() {
		return pereFileNameList;
	}
	public void setPereFileNameList(List<String> pereFileNameList) {
		this.pereFileNameList = pereFileNameList;
	}
	public String getOverSizeFile() {
		return overSizeFile;
	}
	public void setOverSizeFile(String overSizeFile) {
		this.overSizeFile = overSizeFile;
	}
	public String getMaxUploadFileSize() {
		return maxUploadFileSize;
	}
	public void setMaxUploadFileSize(String maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
	}
}
