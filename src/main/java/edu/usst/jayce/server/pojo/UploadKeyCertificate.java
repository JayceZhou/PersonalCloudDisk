package edu.usst.jayce.server.pojo;

// 上传凭证证书
public class UploadKeyCertificate {
	
	private int term;//有效次数
	private String account;//对应用户
	
	// 创建该证书
	public UploadKeyCertificate(int term,String account) {
		this.term=term;
		this.account=account;
	}
	
	// 使用该证书
	public void checked() {
		term--;
	}
	
	// 检查有效性
	public boolean isEffective() {
		return term > 0;
	}
	
	// 得到本证书的创建者
	public String getAccount() {
		return account;
	}

}
