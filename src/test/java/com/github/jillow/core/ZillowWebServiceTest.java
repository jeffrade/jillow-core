package com.github.jillow.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		final JSONObject response = service.getZillowJsonResponse(properties.deepSearchResultsUrl(), "zws-id=" + properties.zwsId() + "&zpid=" + properties.zpid() + "&count=3&rentzestimate=false");
		assertNotNull(response);
		assertTrue(response.length() > 0);
	}
	
	@Test
	public void testGetZillowZestimate() throws Exception{
		final JSONObject response = service.getZillowZestimateJsonResponse(properties.zwsId(), properties.testAddress(), properties.testZip());
		assertNotNull(response);
		assertTrue(response.length() > 0);
		JSONObject jsonObject = response.getJSONObject("amount");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().contains("content"));
		Long amountValue = Long.valueOf(jsonObject.get("content").toString());
		assertNotNull(amountValue);
	}

}
