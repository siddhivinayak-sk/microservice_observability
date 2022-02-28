package com.sk.observabilityms.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/system")
@Tag(name = "Poll", description = "Poll controller")
public class PollController {

	
	@GetMapping("/poll")
	public String poll() {
		return "poll";
	}
	
}
