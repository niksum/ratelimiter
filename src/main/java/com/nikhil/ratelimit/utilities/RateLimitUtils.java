package com.nikhil.ratelimit.utilities;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties
public interface RateLimitUtils {
	 String getUser(HttpServletRequest request);
}
