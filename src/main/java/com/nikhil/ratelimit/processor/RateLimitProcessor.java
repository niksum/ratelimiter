package com.nikhil.ratelimit.processor;

import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule;
import com.nikhil.ratelimit.config.RateLimitResponse;

public interface RateLimitProcessor {
	RateLimitResponse process(Rule rule, String requestKey, Long requestTime);
}
