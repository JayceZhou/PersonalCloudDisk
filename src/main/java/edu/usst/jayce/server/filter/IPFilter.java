package edu.usst.jayce.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.usst.jayce.server.util.ConfigureReader;
import edu.usst.jayce.server.util.IpAddrGetter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.support.WebApplicationContextUtils;

// 阻止特定IP访问过滤器
@WebFilter
@Order(1)
public class IPFilter implements Filter {

	private IpAddrGetter idg;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(filterConfig.getServletContext());
		idg = context.getBean(IpAddrGetter.class);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if(ConfigureReader.instance().enableIPRule()) {
			HttpServletRequest hsr = (HttpServletRequest) request;
			if(ConfigureReader.instance().filterAccessIP(idg.getIpAddr(hsr))) {
				chain.doFilter(request, response);
			}else {
				((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}
