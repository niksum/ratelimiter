package com.nikhil.ratelimit.utilities;

import java.util.Map;
import java.util.Optional;

import com.nikhil.ratelimit.config.URLMapping;

public interface URLMappingFinder {
	
	Optional<URLMapping> getMapping(String path, Map<String, URLMapping> urlMap);
	
}
