package com.nikhil.ratelimit.generator;


import javax.servlet.http.HttpServletRequest;

import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule;
import com.nikhil.ratelimit.config.URLMapping;

public interface RateLimitKeyGenerator {

    String key(HttpServletRequest request, URLMapping mapping, Rule rule);
}
