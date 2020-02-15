package com.nikhil.ratelimit.utilities;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nikhil.ratelimit.config.URLMapping;


@Component
public class URLMappingFinderImpl implements URLMappingFinder{

	@Override
	public Optional<URLMapping> getMapping(String path, Map<String, URLMapping> urlMap) {
		return urlMap.values().stream().filter(mapping -> mapping.getPath().equals(path)).findFirst();
	}

}
