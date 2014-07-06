package com.github.jillow.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.github.jillow.util.ApplicationProperties;
import com.pholser.util.properties.PropertyBinder;

public class ZillowWebServiceTest {
	
	private ZillowApiService service = new ZillowApiService();
	
	private ApplicationProperties properties;

    @Before public void init() throws Exception {
        PropertyBinder<ApplicationProperties> binder = PropertyBinder.forType(ApplicationProperties.class);
        properties = binder.bind(ClassLoader.class.getResourceAsStream("/testApplication.properties"));
    }
	
	@Test
	public void testGetZillowJsonResponse() throws Exception{
		final JSONObject response = service.getZillowJsonResponse(properties.deepSearchResultsUrl(), "zws-id=" + properties.zwsId() + "&address=" + URLEncoder.encode(properties.testAddress(), properties.charset()) + "&citystatezip=" + properties.testZip() + "&rentzestimate=false");
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
	}
	
	@Test
	public void testGetZillowZestimate() throws Exception{
		final JSONObject response = service.getZillowZestimateJsonResponse(properties.zwsId(), properties.testAddress(), properties.testZip());
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
		JSONObject jsonObject = response.getJSONObject("zestimate");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().contains("amount"));
		JSONObject jsonAmount = (JSONObject) jsonObject.get("amount");
		Long amountValue = Long.valueOf(jsonAmount.get("content").toString());
		assertNotNull(amountValue);
	}
	
	@Test
	public void testGetZillowZestimateByZpid() throws Exception{
		final JSONObject response = service.getZillowZestimateJsonResponse(properties.zwsId(), properties.testZpid());
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
		JSONObject jsonObject = response.getJSONObject("zestimate");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().contains("amount"));
		JSONObject jsonAmount = (JSONObject) jsonObject.get("amount");
		Long amountValue = Long.valueOf(jsonAmount.get("content").toString());
		assertNotNull(amountValue);
	}

}
