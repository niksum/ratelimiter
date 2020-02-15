package com.nikhil.ratelimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitResponse {
	private String requestKey;
	private Long remaining;
	private Long reset;
}
