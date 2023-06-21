package edu.usst.jayce.util.file_system_manager.pojo;

import java.util.List;

import edu.usst.jayce.server.model.Node;

// 文件视图（文件管理器专用）
public class FolderView {
	
	private Folder current;//当前文件夹
	
	private List<Folder> folders;//该文件夹内所有文件夹（嵌套的，以便于依次加载）
	private List<Node> files;//该文件夹内所有文件
	
	public Folder getCurrent() {
		return current;
	}
	public void setCurrent(Folder current) {
		this.current = current;
	}
	public List<Folder> getFolders() {
		return folders;
	}
	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}
	public List<Node> getFiles() {
		return files;
	}
	public void setFiles(List<Node> files) {
		this.files = files;
	}
	
	
}
