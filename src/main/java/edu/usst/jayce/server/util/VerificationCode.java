package edu.usst.jayce.server.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

// 随机验证码封装类
public class VerificationCode {

	private String code;
	private BufferedImage image;

	public String getCode() {
		return code.toLowerCase();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void saveTo(String path) throws IOException {
		File f = new File(path);
		ImageIO.write(image, "jpeg", new FileOutputStream(f));
	}
	
	public void saveTo(OutputStream out) throws IOException {
		ImageIO.write(image, "jpeg", out);
	}

}
