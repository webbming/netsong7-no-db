package com.example;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	@GetMapping("/version")
	@ResponseBody
	public String version() {
	    return "Deployed at: " + LocalDateTime.now();
	}
}
