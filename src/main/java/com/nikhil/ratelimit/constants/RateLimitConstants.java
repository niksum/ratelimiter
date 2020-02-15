package com.nikhil.ratelimit.constants;

public final class RateLimitConstants {
	public static final String LIMIT = "limit_";
	public static final String REMAINING = "remaining_";
	public static final String RATE_LIMIT_KEY_PREFIX = "rate_limit";
	
	public static final String RATE_LIMIT_SEPARATOR = "_";
	public static final String RATE_LIMIT_PRE_FILTER = "PRE";
	public static final int RATE_LIMIT_DEFAULT_PRE_ORDER = 1;
	public static final String RATE_LIMIT_POST_FILTER = "POST";
	public static final int RATE_LIMIT_DEFAULT_POST_ORDER = 10;
}
