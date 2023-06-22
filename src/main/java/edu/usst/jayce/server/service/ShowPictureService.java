package edu.usst.jayce.server.service;

import javax.servlet.http.*;

public interface ShowPictureService
{
    String getPreviewPictureJson(final HttpServletRequest request);
    
    // 获取压缩版图片
    void getCondensedPicture(final HttpServletRequest request,final HttpServletResponse response);
}
