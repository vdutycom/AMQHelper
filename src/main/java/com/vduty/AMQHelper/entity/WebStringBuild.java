package com.vduty.AMQHelper.entity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WebStringBuild {
	private StringBuilder sb = new StringBuilder();
	
	public WebStringBuild(String str) {
		this.sb.append(str);
	}
	public WebStringBuild() {
		
	}
	
	public WebStringBuild append(String str) {
		
		if (StringUtils.isNoneBlank(str)) {
			
			this.sb.append("<br/>");
			this.sb.append(str);
			
		}
		
		
		return this;
		
	}
	public String toString() {
		this.sb.append("<br/>");
		sb.append("<a href='/home/' >back home</a>");
		return sb.toString();
	}

}
