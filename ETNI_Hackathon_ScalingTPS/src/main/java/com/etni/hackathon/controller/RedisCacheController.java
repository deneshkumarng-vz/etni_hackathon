package com.etni.hackathon.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etni.hackathon.dao.IpAddressDao;
import com.etni.hackathon.pojo.IpDetails;
import com.etni.hackathon.util.PnoRestTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
public class RedisCacheController {
	
	@Value("${redisHost}")
	private String redisHost;
	
	@Value("${redisPort}")
	private String redisPort;
	
	@Autowired
	IpAddressDao ipAddressDao;
	
	@RequestMapping(value = "/getAndLoadIpAddress", method = { RequestMethod.POST, RequestMethod.GET })
	public String getAndLoadIpAddress(@RequestParam(name = "subOrgId", required = false) String subOrgId,
			@RequestParam(name = "poolGroup", required = false) String poolGroup) {

		//System.out.println("Getting ipAddress and storing into redis...");

		IpDetails invAsAaModel = ipAddressDao.getIpData(subOrgId, poolGroup);

		List<String> ipAddressList = invAsAaModel.getIpAddressList().stream()
				.map(ipData -> ipData.getIpAddressOctet1()
						.concat(ipData.getIpAddressOctet2()
								.concat(ipData.getIpAddressOctet3().concat(ipData.getIpAddressOctet4()))))
				.collect(Collectors.toList());

		Jedis jedis = new Jedis(redisHost, Integer.parseInt(redisPort));

		String[] ipAddressArray = ipAddressList.stream().map(ipData -> ipData).toArray(String[]::new);

		jedis.sadd(subOrgId+"_"+poolGroup, ipAddressArray);
		//System.out.println("Loaded ipAddressList to redis : " + ipAddressList.toString());

		return ipAddressList.toString();
	}

	@RequestMapping(value = "/retrieveIpAddress", method = { RequestMethod.POST, RequestMethod.GET })
	public String popIpAddress(@RequestParam(name = "ipQuantity", required = false) String ipQuantity,
			@RequestParam(name = "subOrgId", required = false) String subOrgId,
			@RequestParam(name = "poolGroup", required = false) String poolGroup) {

		Set<String> ipAddress = new HashSet<>();
		try (JedisPool pool = new JedisPool(redisHost, Integer.parseInt(redisPort))) {
			try (Jedis threadJedis = pool.getResource()) {
				ipAddress = threadJedis.spop(subOrgId+"_"+poolGroup, Integer.parseInt(ipQuantity));
				//System.out.println("IpAdress from redis : " + ipAddress + " Time :" + LocalDateTime.now());
			}
		}
		String ipAddressListString = ipAddress.stream().collect(Collectors.joining(","));

		return ipAddressListString;
	}

	


}
