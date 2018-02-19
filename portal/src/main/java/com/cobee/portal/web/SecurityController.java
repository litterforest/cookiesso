package com.cobee.portal.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cobee.portal.vo.Ticket;

@Controller
public class SecurityController {
	
	private static final String AUTHCENTER_URL = "http://www.cobee.com:8066";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping(value = "/loginForm")
	public String loginForm(String redirectUrl, Model model)
	{
		model.addAttribute("redirectUrl", redirectUrl);
		return "login";
	}
	
	@PostMapping(value = "/doLogin")
	public String doLogin(HttpServletRequest request, HttpServletResponse response, String username, String password, String redirectUrl, RedirectAttributes redirectAttributes)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("name", username);
		map.add("password", password);
		HttpEntity<MultiValueMap<String, String>> restRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		Ticket ticket = restTemplate.postForObject(AUTHCENTER_URL + "/authcenter/Auth/login", restRequest, Ticket.class);
		String resultView = "";
		if (ticket == null)
		{
			redirectAttributes.addFlashAttribute("msg", "用户名或密码错误");
			resultView = "redirect:/loginForm";
		}
		else
		{
			Cookie cookie = new Cookie("user-ticket", ticket.getId());
			// 不保存cookie数据
			cookie.setMaxAge(-1);
			cookie.setPath("/");
			cookie.setDomain(".cobee.com");
			response.addCookie(cookie);
			request.getSession().setAttribute("user", ticket.getUser());
			request.getSession().setAttribute("ticket", ticket.getId());
			if (StringUtils.isNotBlank(redirectUrl))
			{
				resultView = "redirect:" + redirectUrl;
			}
			else
			{
				resultView = "redirect:/main";
			}
			
		}
		return resultView;
	}
	
	@GetMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response)
	{
		// 1,清空session
		request.getSession().invalidate();
		
		// 2,清空客户端ticket cookie
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		if (cookies != null && cookies.length > 0)
		{
			for (Cookie ck : cookies)
			{
				if (StringUtils.equals(ck.getName(), "user-ticket"))
				{
					cookie = ck;
					break;
				}
			}
		}
		if (cookie != null)
		{
			
			// 3,清除认证中心的ticket值
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("ticket", cookie.getValue());
			HttpEntity<MultiValueMap<String, String>> restRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			restTemplate.postForObject(AUTHCENTER_URL + "/authcenter/Auth/logout", restRequest, Void.class);
			
			cookie.setValue(null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setDomain(".cobee.com");
			response.addCookie(cookie);
			
		}
		
		return "redirect:/main";
	}
	
}
