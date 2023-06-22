package edu.usst.jayce.server.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;
import org.springframework.stereotype.Component;

// 文本编码集判断器
@Component
public class TxtCharsetGetter {

	// 获取文本输入流的编码集
	public String getTxtCharset(InputStream in) throws Exception {
		int lang = nsPSMDetector.CHINESE;
		nsDetector det = new nsDetector(lang);
		CharsetDetectionObserverImpl cdoi = new CharsetDetectionObserverImpl();
		det.Init(cdoi);
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[1024];
		int len;
		boolean isAscii = true;
		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			if (isAscii) {
				isAscii = det.isAscii(buf, len);
			}
			if (!isAscii) {
				if (det.DoIt(buf, len, false)) {
					break;
				}
			}
		}
		imp.close();
		in.close();
		det.DataEnd();
		if (isAscii) {
			return "ASCII";
		} else if (cdoi.getCharset() != null) {
			return cdoi.getCharset();
		} else {
			String[] prob = det.getProbableCharsets();
			if (prob != null && prob.length > 0) {
				return prob[0];
			}
			return "GBK";
		}
	}

	// 获取文本输入流的编码集
	public String getTxtCharset(byte[] buf, int offset, int length) throws Exception {
		int lang = nsPSMDetector.CHINESE;
		nsDetector det = new nsDetector(lang);
		CharsetDetectionObserverImpl cdoi = new CharsetDetectionObserverImpl();
		det.Init(cdoi);
		boolean isAscii = true;
		byte[] array = Arrays.copyOfRange(buf, offset, (offset + length));
		if (isAscii) {
			isAscii = det.isAscii(array, length);
		}
		if (!isAscii) {
			det.DoIt(array, length, false);
		}
		det.DataEnd();
		if (isAscii) {
			return "ASCII";
		} else if (cdoi.getCharset() != null) {
			return cdoi.getCharset();
		} else {
			String[] prob = det.getProbableCharsets();
			if (prob != null && prob.length > 0) {
				return prob[0];
			}
			return "GBK";
		}
	}

}
