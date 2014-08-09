package com.github.jillow.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.github.jillow.util.ApplicationProperties;
import com.github.jillow.util.ChartUnitType;
import com.pholser.util.properties.PropertyBinder;

public class ZillowApiServiceTest {
	
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
	
	@Test
	public void testGetUpdatedPropertyDetails() throws Exception{
		final JSONObject response = service.getUpdatedPropertyDetailsJson(properties.zwsId(), properties.testZpid());
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
		JSONObject jsonObject = response.getJSONObject("UpdatedPropertyDetails:updatedPropertyDetails");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().contains("response"));
		JSONObject jsonResponseObject = (JSONObject) jsonObject.get("response");
		assertNotNull(jsonResponseObject);
	}
	
	@Test
	public void testGetZillowZpidByAddress() throws Exception{
		final JSONObject response = service.getZillowZpidByAddress(properties.zwsId(), properties.testAddress(), properties.testZip());
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
		Object jsonObject = response.get("zpid");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().length() > 0);
		assertTrue(Long.valueOf(jsonObject.toString()).longValue() > 0);
	}
	
	@Test
	public void testGetJsonNthChildObject() throws Exception{
		final String jsonString = "{'firstNode':{'secondNode':{'lastNode':{'lastKey':'lastValue'}}}}";
		JSONObject json = new JSONObject(jsonString);
		JSONObject returnObject = service.getJsonNthChildObject(json, new String[]{"firstNode","secondNode"}, "lastNode");
		assertNotNull(returnObject);
		assertEquals(returnObject.toString(), "{\"lastNode\":{\"lastKey\":\"lastValue\"}}");
		JSONObject lastNodeJson = returnObject.getJSONObject("lastNode");
		assertEquals(lastNodeJson.get("lastKey").toString(), "lastValue");
	}
	
	@Test
	public void testGetChartJson() throws Exception{
		final JSONObject response = service.getChartJson(properties.zwsId(), properties.testZpid(), ChartUnitType.DOLLAR, 300, 300);
		assertNotNull(response);System.out.println(response.toString());
		assertTrue(response.length() > 0);
		assertTrue(!response.toString().toLowerCase().contains("error"));
		Object jsonObject = response.get("Chart:chart");
		assertNotNull(jsonObject);
		assertTrue(jsonObject.toString().length() > 0);
	}

}
