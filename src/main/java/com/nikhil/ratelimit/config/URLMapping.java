package com.nikhil.ratelimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class URLMapping {
	private String id;
	private String url;
	private String path;
	
	public URLMapping(String url, String path){
		this.path = path;
		this.url = url;
		this.id = path.startsWith("/")?path.substring(1):path;
	}
}
