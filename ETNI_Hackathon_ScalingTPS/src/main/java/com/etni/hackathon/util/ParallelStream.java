package com.etni.hackathon.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;


@Component
public class ParallelStream {
	
	@Autowired
	public static PnoRestTemplate pnoRestTemplate;
	
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.getLogger("root").setLevel(Level.WARN);
		
		IntStream.range(1, 100).parallel().forEach(i -> {
			String url = "http://localhost:9999/etniHackathon/retrieveIpAddress?subOrgId=E0002568660zzzzzzzzzzzzzz&poolGroup=CRINTCBBB&ipQuantity=1";
			MultiValueMap<String, String> headerParams = new LinkedMultiValueMap<String, String>();
			Map<String, Object> attributesObject = new HashMap<String, Object>();

			String response = pnoRestTemplate.invokePNOAPIForParellelCalls(args, null, null, url, headerParams, headerParams, HttpMethod.POST, MediaType.APPLICATION_JSON);
			System.out.println("Retrived ipAddress : "+response+"-  Time : "+LocalDateTime.now());
		});
		
	}

}
