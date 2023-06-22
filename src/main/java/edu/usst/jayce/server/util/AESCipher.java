package edu.usst.jayce.server.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

// AES加密器
@Component
public class AESCipher {

	private static final String CIPHER_TYPE = "AES";//所用到的加密算法类型
	private Base64.Encoder encoder;
	private Base64.Decoder decoder;
	
	public AESCipher() {
		encoder = Base64.getEncoder();
		decoder = Base64.getDecoder();
	}
	
	// 生成随机AES密钥
	public String generateRandomKey() throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance(CIPHER_TYPE);
		kg.init(128);
		return encoder.encodeToString(kg.generateKey().getEncoded());
	}
	
	//对字符串进行AES加密
	public String encrypt(String base64Key, String content) throws Exception {
		SecretKey key = new SecretKeySpec(decoder.decode(base64Key), CIPHER_TYPE);
		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return encoder.encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
	}
	
	// 对密文进行AES解密
	public String decrypt(String base64Key, String ciphertext) throws Exception {
		SecretKey key = new SecretKeySpec(decoder.decode(base64Key), CIPHER_TYPE);
		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(decoder.decode(ciphertext)),StandardCharsets.UTF_8);
	}

}
