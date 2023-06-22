package edu.usst.jayce.server.service;

import javax.servlet.http.*;

public interface PlayVideoService
{
	// 解析播放视频文件
    String getPlayVideoJson(final HttpServletRequest request);
}
