package com.cobee.blog.component.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cobee.blog.vo.User;

/**
 * Servlet Filter implementation class LoginAuthFilter
 */
public class LoginAuthFilter implements Filter {

	private static final String PORTAL_URL = "http://www.cobee.com:8055/portal";
	private static final String AUTHCENTER_URL = "http://www.cobee.com:8066/authcenter";
	private static final Map<String, List<String>> interceptMap = new LinkedHashMap<>();
	static
	{
		List<String> anon = new ArrayList<String>();
		anon.add("/index");
		interceptMap.put("anon", anon);
		List<String> auth = new ArrayList<String>();
		auth.add("/blog");
		interceptMap.put("auth", auth);
	}
	
	private FilterConfig filterConfig;
	private RestTemplate restTemplate;
	
    /**
     * Default constructor. 
     */
    public LoginAuthFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// 当前请求的路径
		String fullRequestURI = "http://blog.cobee.com:8077" + httpRequest.getRequestURI();
		fullRequestURI = URLEncoder.encode(fullRequestURI, "UTF-8");
		
		// 1,从客户端cookie里面查找ticket数据
		Cookie[] cookies = httpRequest.getCookies();
		String ticket = "";
		if (cookies != null && cookies.length > 0)
		{
			for (Cookie ck : cookies)
			{
				if (StringUtils.equals(ck.getName(), "user-ticket"))
				{
					ticket = ck.getValue();
					break;
				}
			}
		}
		// 2,ticket数据不存在,重定向到portal系统登录
		if (StringUtils.isBlank(ticket))
		{
			httpRequest.getSession().removeAttribute("user");
			httpResponse.sendRedirect(PORTAL_URL + "/loginForm?redirectUrl=" + fullRequestURI);
			return;
		}
		else
		{
			User user = (User) httpRequest.getSession().getAttribute("user");
			// 如果session user为空，说明没有拿ticket到认证系统认证
			if (user == null)
			{
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("ticket", ticket);
				HttpEntity<MultiValueMap<String, String>> restRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				User ticketUser = restTemplate.postForObject(AUTHCENTER_URL + "/Auth/loginByTicket", restRequest, User.class);
				// ticket认证成功
				if (ticketUser != null)
				{
					httpRequest.getSession().setAttribute("user", ticketUser);
				}
				// ticket认证失败，重定向到portal系梳登录
				else
				{
					httpResponse.sendRedirect(PORTAL_URL + "/loginForm?redirectUrl=" + fullRequestURI);
					return;
				}
				
			}
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		filterConfig = fConfig;
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(fConfig.getServletContext());
		restTemplate = ctx.getBean(RestTemplate.class);
	}

}
