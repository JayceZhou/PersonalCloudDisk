package edu.usst.jayce.server.service;

import javax.servlet.http.*;

public interface AccountService
{
    String checkLoginRequest(final HttpServletRequest request, final HttpSession session);
    
    void logout(final HttpSession session);
    
    String getPublicKey();
    
    void getNewLoginVerCode(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session);
    
    //应答响应逻辑
    String doPong(final HttpServletRequest request);
    
    // 修改账户的密码
    String changePassword(final HttpServletRequest request);
    
    //获取是否允许自由注册新账户
    String isAllowSignUp();
    
    // 执行账户注册
    String doSignUp(final HttpServletRequest request);
}
