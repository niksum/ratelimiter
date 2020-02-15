package com.nikhil.ratelimit.utilities;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/*
 * RateLimit utils method at present is just fetching the user header information and takeing it as user.
 * It can be enhanced to to provide more specific implemention depending upon the needs. Like authenticated token etc..
 */
@Component
public class RateLimitUtilsImpl implements RateLimitUtils{
	
	public String getUser(HttpServletRequest request) {
		 return request.getHeader("user") != null ? request.getHeader("user") : "UNKNOWN";
		
	}

}
