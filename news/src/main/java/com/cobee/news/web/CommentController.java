package com.cobee.news.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentController {
	
	@GetMapping(value = "/comment")
	public String comment()
	{
		return "comment";
	}
	
}
