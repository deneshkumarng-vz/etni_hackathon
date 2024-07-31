package com.etni.hackathon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



@Component
public class PnoRestTemplate {

	public static final Logger logger = LoggerFactory.getLogger(PnoRestTemplate.class);


	public  static <T> T invokePNOAPI(Object requestObject, String typeDef, Class<T> responseType, String url,
			@SuppressWarnings("rawtypes") MultiValueMap requestParams, MultiValueMap<String, String> headerParams,
			HttpMethod methodType, MediaType mediaType) {
		long start = System.currentTimeMillis();

		if (StringUtils.isEmpty(methodType)) {
			methodType = HttpMethod.GET;
		}
		if (StringUtils.isEmpty(mediaType)) {
			mediaType = MediaType.APPLICATION_JSON;
		}

		// create REST API headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.addAll(headerParams);

		// Create httpEntity with requestObject and headers
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(requestObject, headers);

		// Building URI with Params
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).queryParams(requestParams).build().encode();

		// REST API call to PNO for executing typeDef details

		ResponseEntity<String> responseEntity = null;
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			if (requestObject.toString().length() < UtilConstants.PNO_LOGGING_LIMIT)
				logger.info("PNO Invocation - typedef :{} .. with params : {}", typeDef, requestObject.toString());
			responseEntity = restTemplate.exchange(uriComponents.toUri(), methodType, httpEntity, String.class);

			long duration = System.currentTimeMillis() - start;

			// if(responseEntity.getBody().length()<UtilConstants.PNO_LOGGING_LIMIT)
			// logger.info("PNO Invocation - typedef :{} .. Time taken for PNO call {}",
			// typeDef, duration);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {

				JsonNode actualObj = objectMapper
						.readTree(com.google.json.JsonSanitizer.sanitize(responseEntity.getBody()));
				JsonNode resultsObj = actualObj.get("results");
				JsonNode statusObj = actualObj.get("status");

				T object = null;

				if ("200".equals(statusObj.get("code").asText())) {
					if (responseType != null) {
						object = objectMapper.treeToValue(resultsObj, responseType);
					} else {
						object = (T) statusObj.get("code").asText();
					}
					if (resultsObj.toString().length() < UtilConstants.PNO_LOGGING_LIMIT)
						logger.info("PNO Invocation - typedef :{} ..  Results : {} ", typeDef, resultsObj);
					else
						logger.info(
								"PNO Invocation - typedef :{} ..  Results : Success. Result Content is too long and can not be logged",
								typeDef);
				} else {
					logger.error(
							"PNO Invocation - Response for typedef {} is not success with input : {} and status message is : {}",
							typeDef, requestObject.toString(), statusObj.get("message").asText());
				}
				return object;

			} else {
				logger.error("PNO Invocation - HttpStatus not OK for typeDef : {} with params : {} ", typeDef,
						requestObject.toString());
				return null;
			}
		} catch (Exception ex) {
			logger.error("Exception at class: PNORestTemplate for typedef : {} with params : {} and error is : {} ",
					typeDef, requestObject.toString(), ex);
			return null;
		}

	}
	
	
	
	
	
	
	
	public  static  String invokePNOAPIForParellelCalls(Object requestObject, String typeDef, String responseType, String url,
			@SuppressWarnings("rawtypes") MultiValueMap requestParams, MultiValueMap<String, String> headerParams,
			HttpMethod methodType, MediaType mediaType) {
		long start = System.currentTimeMillis();

		if (StringUtils.isEmpty(methodType)) {
			methodType = HttpMethod.GET;
		}
		if (StringUtils.isEmpty(mediaType)) {
			mediaType = MediaType.APPLICATION_JSON;
		}

		// create REST API headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.addAll(headerParams);

		// Create httpEntity with requestObject and headers
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(requestObject, headers);

		// Building URI with Params
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).queryParams(requestParams).build().encode();

		// REST API call to PNO for executing typeDef details

		ResponseEntity<String> responseEntity = null;
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			if (requestObject.toString().length() < UtilConstants.PNO_LOGGING_LIMIT)
			responseEntity = restTemplate.exchange(uriComponents.toUri(), methodType, httpEntity, String.class);

			long duration = System.currentTimeMillis() - start;

			// if(responseEntity.getBody().length()<UtilConstants.PNO_LOGGING_LIMIT)
			// logger.info("PNO Invocation - typedef :{} .. Time taken for PNO call {}",
			// typeDef, duration);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {

				return responseEntity.getBody().toString();

			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
		
	}


}
