package edu.usst.jayce.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

// Txt转PDF工具
@Component
public class Txt2PDFUtil {
	
	@Resource
	private TxtCharsetGetter tcg;
	
	// 执行word格式转换（docx）
	public void convertPdf(File in, OutputStream out) throws Exception {
		Rectangle rect = new Rectangle(PageSize.A4);// 以A4页面显示文本
		Document doc = new Document(rect);
		PdfWriter pw = PdfWriter.getInstance(doc, out);// 开始转换
		doc.open();
		BaseFont songFont = BaseFont.createFont(
				ConfigureReader.instance().getPath() + File.separator + "fonts/wqy-zenhei.ttc,0", BaseFont.IDENTITY_H,
				BaseFont.NOT_EMBEDDED);
		Font font = new Font(songFont, 12, Font.NORMAL);// 设置字体格式
		Paragraph paragraph = new Paragraph();
		paragraph.setFont(font);
		String charset = tcg.getTxtCharset(new FileInputStream(in));// 判断编码格式
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(in), charset));
		String line = null;
		while ((line = reader.readLine()) != null) {
			paragraph.add(line + "\n");// 将文本逐行写入PDF段落
		}
		reader.close();
		// 避免因打开空文档可能造成的打开失败
		if (paragraph.isEmpty()) {
			paragraph.add("");
		}
		doc.add(paragraph);// 写入段落至文档
		doc.close();// 关闭文档
		pw.flush();
		pw.close();
	}

}
