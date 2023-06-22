package edu.usst.jayce.server.service;

import javax.servlet.http.*;

public interface PlayAudioService
{
    String getAudioInfoListByJson(final HttpServletRequest request);
}
