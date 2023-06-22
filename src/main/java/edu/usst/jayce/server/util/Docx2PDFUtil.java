package edu.usst.jayce.server.util;

import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

// Docx转PDF工具
@Component
public class Docx2PDFUtil {
	
	// 执行word格式转换（docx）
	public void convertPdf(InputStream in, OutputStream out) throws Exception {
		XWPFDocument document = new XWPFDocument(in);
		//获取系统已安装的所有字体
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (XWPFParagraph p : document.getParagraphs()) {
			for (XWPFRun r : p.getRuns()) {
				//判断文档中的字体是否安装
				if (Arrays.stream(ge.getAvailableFontFamilyNames()).parallel()
						.anyMatch((e) -> e.equals(r.getFontFamily()))) {
					continue;
				}
				//如未安装，则使用程序自带的“文泉驿正黑”替代
				r.setFontFamily("WenQuanYi Zen Hei");
			}
		}
		PdfConverter.getInstance().convert(document, out, PdfOptions.create().fontProvider(Docx2PDFFontProvider.getInstance()));
	}

}
