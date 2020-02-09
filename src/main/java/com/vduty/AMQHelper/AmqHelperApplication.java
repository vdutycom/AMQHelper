package com.vduty.AMQHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class AmqHelperApplication {

	 private static final Logger LOG = LoggerFactory.getLogger(AmqHelperApplication.class);
	public static void main(String[] args) {
		
		SpringApplication.run(AmqHelperApplication.class, args);
		LOG.info("\r\n\n\n\n\n\n\n************ start succeffully **************\n\n\n\n\n\n");
	}

}
