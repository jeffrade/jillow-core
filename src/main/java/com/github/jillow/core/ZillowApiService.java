package com.github.jillow.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jillow.util.ApplicationProperties;
import com.github.jillow.util.ChartUnitType;
import com.pholser.util.properties.PropertyBinder;

public class ZillowApiService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZillowApiService.class);
	
	private ApplicationProperties properties;
	
	public ZillowApiService(){
		super();
		init();
	}

    private void init() {
        PropertyBinder<ApplicationProperties> binder = PropertyBinder.forType(ApplicationProperties.class);
        try {
			properties = binder.bind(ClassLoader.class.getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			LOG.error("Error while binding", e);
		}
    }
    
    public JSONObject getZillowZpidByAddress(final String zwsId, final String address, final String cityStateZip){
    	LOG.debug("Entering...");
		final String parameterQuery = buildGetDeepSearchResultsParameterQuery(zwsId, address, cityStateZip, false);
		JSONObject jsonResponse = getZillowJsonResponse(properties.deepSearchResultsUrl(), parameterQuery);

		return getJsonNthChildObject(jsonResponse
			, new String[]{
				"SearchResults:searchresults"
				,"response"
				,"results"
				,"result"}
			, "zpid");
    }

	public JSONObject getZillowZestimateJsonResponse(final String zwsId, final String address, final String cityStateZip) {
		LOG.debug("Entering...");
		final String parameterQuery = buildGetDeepSearchResultsParameterQuery(zwsId, address, cityStateZip, false);
		JSONObject jsonResponse = getZillowJsonResponse(properties.deepSearchResultsUrl(), parameterQuery);

		return getJsonNthChildObject(jsonResponse
			, new String[]{
				"SearchResults:searchresults"
				,"response"
				,"results"
				,"result"}
			, "zestimate");
	}
	
	public JSONObject getZillowZestimateJsonResponse(final String zwsId, final String zpid) {
		LOG.debug("Entering...");
		final String parameterQuery = buildZpidParameterQuery(zwsId, zpid);
		JSONObject jsonResponse = getZillowJsonResponse(properties.zestimateUrl(), parameterQuery);
		
		return getJsonNthChildObject(jsonResponse
				, new String[]{
					"Zestimate:zestimate"
					,"response"}
				, "zestimate");
	}
	
	public JSONObject getDeepSearchResultsJson(final String zwsId, final String address, final String cityStateZip, final boolean rentZestimate){
		LOG.debug("Entering...");
		final String parameterQuery = buildGetDeepSearchResultsParameterQuery(zwsId, address, cityStateZip, rentZestimate);
		return getZillowJsonResponse(properties.deepSearchResultsUrl(), parameterQuery);
	}
	
	public JSONObject getDeepCompsJson(final String zwsId, final String zpid, final int count, final boolean rentZestimate){
		LOG.debug("Entering...");
		final String parameterQuery = buildGetDeepCompsParameterQuery(zwsId, zpid, count, rentZestimate);
		return getZillowJsonResponse(properties.deepCompsUrl(), parameterQuery);
	}

	public JSONObject getUpdatedPropertyDetailsJson(final String zwsId, final String zpid){
		LOG.debug("Entering...");
		final String parameterQuery = buildZpidParameterQuery(zwsId, zpid);
		return getZillowJsonResponse(properties.updatedPropertyDetailsUrl(), parameterQuery);
	}
	
	public JSONObject getChartJson(final String zwsId, final String zpid, final ChartUnitType chartUnitType, final int height, final int width){
		LOG.debug("Entering...");
		final String parameterQuery = buildChartParameterQuery(zwsId, zpid, chartUnitType.toString(), String.valueOf(height), String.valueOf(width));
		return getZillowJsonResponse(properties.chartUrl(), parameterQuery);
	}

	protected JSONObject getZillowJsonResponse(final String baseURL, final String parameterQuery) {
		LOG.debug("Entering...");
		URLConnection connection = null;
		String responseString = null;
		JSONObject jsonObj = null;
		
		try {
			final String url = baseURL + properties.urlParamSeparator() + parameterQuery;
			LOG.debug(url);
			connection = new URL(url).openConnection();
			connection.setRequestProperty("Accept-Charset", properties.charset());
			final InputStream response = connection.getInputStream();
			responseString = IOUtils.toString(response, properties.charset());
			jsonObj = XML.toJSONObject(responseString);
			LOG.debug(jsonObj.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonObj;
	}
	
	protected String buildGetDeepSearchResultsParameterQuery(String zwsId, String address, String cityStateZip, boolean rentZestimate){
		LOG.debug("Entering...");
		String parameterQuery = null;
		
		try {
			parameterQuery = String.format("zws-id=%s&address=%s&citystatezip=%s&rentzestimate=%s", 
				     URLEncoder.encode(zwsId, properties.charset()), 
				     URLEncoder.encode(address, properties.charset()), 
				     URLEncoder.encode(cityStateZip, properties.charset()), 
				     String.valueOf(rentZestimate));
			LOG.debug(parameterQuery);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return parameterQuery;
	}

	protected String buildGetDeepCompsParameterQuery(String zwsId, String zpid, int count, boolean rentZestimate) {
		LOG.debug("Entering...");
		String parameterQuery = null;
		
		try {
			parameterQuery = String.format("zws-id=%s&zpid=%s&count=%s&rentzestimate=%s", 
				     URLEncoder.encode(zwsId, properties.charset()), 
				     URLEncoder.encode(zpid, properties.charset()), 
				     URLEncoder.encode(String.valueOf(count), properties.charset()), 
				     String.valueOf(rentZestimate));
			LOG.debug(parameterQuery);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return parameterQuery;
	}
	
	protected String buildZpidParameterQuery(String zwsId, String zpid) {
		LOG.debug("Entering...");
		String parameterQuery = null;
		
		try {
			parameterQuery = String.format("zws-id=%s&zpid=%s", 
				     URLEncoder.encode(zwsId, properties.charset()), 
				     URLEncoder.encode(zpid, properties.charset()));
			LOG.debug(parameterQuery);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return parameterQuery;
	}

	protected String buildChartParameterQuery(final String zwsId, final String zpid, final String chartUnitType, final String height, final String width) {
		//http://www.zillow.com/webservice/GetChart.htm
		//?zws-id=<ZWSID>&unit-type=percent&zpid=48749425&width=300&height=150
		LOG.debug("Entering...");
		String parameterQuery = null;
		
		try {
			parameterQuery = String.format("zws-id=%s&zpid=%s&unit-type=%s&width=%s&height=%s", 
				     URLEncoder.encode(zwsId, properties.charset()), 
				     URLEncoder.encode(zpid, properties.charset()), 
				     URLEncoder.encode(chartUnitType, properties.charset()), 
				     URLEncoder.encode(height, properties.charset()), 
				     URLEncoder.encode(width, properties.charset()));
			LOG.debug(parameterQuery);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return parameterQuery;
	}
	
	protected JSONObject getJsonNthChildObject(final JSONObject json, final String[] parentNodes, final String targetNode){
		LOG.debug("Entering...");
		JSONObject tempObject = json;
		
		for(String parentNode : parentNodes){
			tempObject = (JSONObject) tempObject.get(parentNode);
		}
		
		return new JSONObject(tempObject, new String[]{targetNode});
	}

}
