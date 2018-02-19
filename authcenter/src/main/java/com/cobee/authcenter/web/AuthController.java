package com.cobee.authcenter.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cobee.authcenter.vo.Ticket;
import com.cobee.authcenter.vo.User;

@RestController
@RequestMapping("/Auth")
public class AuthController {
	
	private static final Map<String, User> userMap = new HashMap<String, User>();
	private static final Map<String, User> ticketMap = new HashMap<String, User>();
	
	static
	{
		userMap.put("cobee", new User("cobee", "123456"));
		userMap.put("jone", new User("jone", "123456"));
	}
	
	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Ticket login(String name, String password)
	{
		User user = userMap.get(name);
		Ticket ticket = null;
		if (user != null)
		{
			if (StringUtils.equals(user.getPassword(), password))
			{
				String uuid = UUID.randomUUID().toString();
				ticketMap.put(uuid, user);
				ticket = new Ticket();
				ticket.setId(uuid);
				ticket.setUser(user);
			}
		}
		return ticket;
	}
	
	@PostMapping(value = "/loginByTicket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User login(String ticket)
	{
		User user = ticketMap.get(ticket);
		return user;
	}
	
	@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void logout(String ticket)
	{
		System.out.println("==============================logout():ticket:" + ticket);
		ticketMap.remove(ticket);
	}
	
	@GetMapping(value = "/test")
	public String test()
	{
		return "success";
	}
	
}
