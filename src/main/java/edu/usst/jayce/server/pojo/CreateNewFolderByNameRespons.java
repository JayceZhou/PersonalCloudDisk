package edu.usst.jayce.server.pojo;

// 上传文件夹时，“保留两者”操作使用新文件夹名的响应信息封装
public class CreateNewFolderByNameRespons {
	
	private String result;
	private String newName;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}

}
