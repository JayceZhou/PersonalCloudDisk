package edu.usst.jayce.server.util;

import com.lowagie.text.FontFactory;

import fr.opensagres.xdocreport.itext.extension.font.AbstractFontRegistry;

// Docx转PDF字体格式封装类
public class Docx2PDFFontProvider extends AbstractFontRegistry{
	
	private static Docx2PDFFontProvider instance;
	
	private Docx2PDFFontProvider() {
		FontFactory.setFontImp(new Docx2PDFFontFactory());
	}

	@Override
	protected String resolveFamilyName(String arg0, int arg1) {
		return arg0;
	}
	
	// 取得字体封装类的唯一实例
	public static Docx2PDFFontProvider getInstance() {
		if(instance==null) {
			instance=new Docx2PDFFontProvider();
		}
		return instance;
	}

}
