package com.nikhil.ratelimit.config;

import javax.servlet.http.HttpServletRequest;

import com.nikhil.ratelimit.utilities.RateLimitUtils;

/*
 * RateLimitType :- This enum can be updated to include other way of defining Rule for API throttling.
 * At present it is per user base. Other example can be Location based, IP based, Authentication based etc
 */
public enum RateLimitType {
	
	USER {
        @Override
        public boolean applyRateLimit(HttpServletRequest request,URLMapping mapping, RateLimitUtils rateLimitUtils, String matcher) {
            return matcher.equals(rateLimitUtils.getUser(request));
        }

        @Override
        public String key(HttpServletRequest request, URLMapping mapping, RateLimitUtils rateLimitUtils, String matcher) {
            return rateLimitUtils.getUser(request);
        }
    };
    

	public abstract boolean applyRateLimit(HttpServletRequest request, URLMapping mapping,
            RateLimitUtils rateLimitUtils, String matcher);
	
	 public abstract String key(HttpServletRequest request, URLMapping mapping,
             RateLimitUtils rateLimitUtils, String matcher);
}
