package edu.usst.jayce.server.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

// 请求IP地址解析工具
@Component
public class IpAddrGetter {
	
	// 可能的转发标识请求头名称
	private String[] ipAddrHeaders = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
			"HTTP_X_FORWARDED_FOR" };

	// 获得请求来源的IP地址（公网）
	public String getIpAddr(HttpServletRequest request) {
		if(ConfigureReader.instance().isIpXFFAnalysis()) {
			for (String ipAddrHeader : ipAddrHeaders) {
				String ipAddress = request.getHeader(ipAddrHeader);
				if (ipAddress != null && ipAddress.length() > 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
					int indexOfIpSeparator = ipAddress.indexOf(",");
					if (indexOfIpSeparator >= 0) {
						return ipAddress.substring(0, indexOfIpSeparator).trim();
					} else {
						return ipAddress.trim();
					}
				}
			}
		}
		String remoteAddr = request.getRemoteAddr();
		if(remoteAddr != null) {
			return request.getRemoteAddr().trim();
		}else {
			return "获取失败";
		}
	}

}
