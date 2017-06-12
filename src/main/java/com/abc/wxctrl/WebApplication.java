
package com.abc.wxctrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.abc.wxctrl.config.CommonBeanConfig;
import com.abc.wxctrl.config.WebSocketConfig;

@SpringBootApplication
public class WebApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}



