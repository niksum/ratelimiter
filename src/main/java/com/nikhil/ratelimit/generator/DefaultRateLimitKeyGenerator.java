/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nikhil.ratelimit.generator;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule;
import com.nikhil.ratelimit.config.URLMapping;
import com.nikhil.ratelimit.constants.RateLimitConstants;
import com.nikhil.ratelimit.utilities.RateLimitUtils;

/*
 * Key generation to get a unique key to find out its API calling history and take a decision
 */
@RequiredArgsConstructor
public class DefaultRateLimitKeyGenerator implements RateLimitKeyGenerator {

    private final RateLimitUtils rateLimitUtils;

	@Override
	public String key(HttpServletRequest request, URLMapping mapping, Rule rule) {
		StringBuilder builder = new StringBuilder();
		builder.append(RateLimitConstants.RATE_LIMIT_KEY_PREFIX);
		
		if(mapping !=null){
			builder.append(RateLimitConstants.RATE_LIMIT_SEPARATOR);
			builder.append(mapping.getId());
			
		}
		rule.getType().forEach(type -> {
			String key = type.key(request, mapping, rateLimitUtils);
			
			  if (key!=null && !key.isEmpty()) {
				  	builder.append(RateLimitConstants.RATE_LIMIT_SEPARATOR);
	                builder.append(key);
			  }
	            
	        });
	        return builder.toString();
	    }
		
	
}
