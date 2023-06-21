package edu.usst.jayce.server.pojo;

import java.util.List;

import edu.usst.jayce.server.model.Folder;
import edu.usst.jayce.server.model.Node;

// 加载后续文件列表数据所用的封装类
public class RemainingFolderView {
	
	private List<Folder> folderList;
	private List<Node> fileList;
	
	public List<Folder> getFolderList() {
		return folderList;
	}
	public void setFolderList(List<Folder> folderList) {
		this.folderList = folderList;
	}
	public List<Node> getFileList() {
		return fileList;
	}
	public void setFileList(List<Node> fileList) {
		this.fileList = fileList;
	}
}
