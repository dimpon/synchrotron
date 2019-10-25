package org.synchrotron.autoconfigure.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greeting() {
		return "greeting";
	}

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greeting(String message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return "Hello, " + HtmlUtils.htmlEscape(message) + "!";
	}

}

