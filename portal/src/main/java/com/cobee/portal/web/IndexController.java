package com.cobee.portal.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	
	@GetMapping(value = "/main")
	public String index(Model model)
	{
		return "main";
	}
	
}
