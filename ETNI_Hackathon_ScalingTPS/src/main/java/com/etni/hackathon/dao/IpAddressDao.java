package com.etni.hackathon.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.etni.hackathon.pojo.IpDetails;
import com.etni.hackathon.util.PnoRestTemplate;

@Service
public class IpAddressDao {
	
	public IpDetails getIpData(String subOrgId, String poolGroup) {

		IpDetails ipDetails = new IpDetails();
		PnoRestTemplate pnoRestTemplate = new PnoRestTemplate();
		String pnoUrl = "https://mcscmpt2-pnoetni.ebiz.verizon.com/PNO/request";

		Map<String, Object> attributesObject = new HashMap<String, Object>();
		attributesObject.put("subOrgId", subOrgId);
		attributesObject.put("poolGroup", poolGroup);

		Map requestObject = new HashMap();
		requestObject.put("keyAttributes", attributesObject);
		requestObject.put("requestType", "Test_COR_GetIpAddress");

		MultiValueMap<String, String> headerParams = getHeadersMap();

		try {
			ipDetails = pnoRestTemplate.invokePNOAPI(requestObject, "Test_COR_GetIpAddress", IpDetails.class, pnoUrl,
					null, headerParams, HttpMethod.POST, MediaType.APPLICATION_JSON);

		} catch (Exception e) {
			System.out.println("Error occured, while calling " + "Test_COR_GetIpAddress" + " typedef" + e);
		}

		return ipDetails;
	}
	
	private MultiValueMap<String, String> getHeadersMap() {

		MultiValueMap<String, String> headerParams = new LinkedMultiValueMap<String, String>();

		headerParams.set("ORIGINAL_SERVICE", "submitMasterOrder");
		headerParams.set("CLIENT_ID", "VZ-DIGITAL");
		headerParams.set("ORIGINAL_SUBSERVICE", "test");
		headerParams.set("CORRELATION_ID", "hackathon");

		return headerParams;
	}
	

}
