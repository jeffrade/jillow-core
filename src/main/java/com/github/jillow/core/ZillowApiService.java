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

	public JSONObject getZillowZestimateJsonResponse(final String zwsId, final String address, final String cityStateZip) {
		final String parameterQuery = buildGetDeepSearchResultsParameterQuery(zwsId, address, cityStateZip, false);
		JSONObject jsonResponse = getZillowJsonResponse(properties.deepSearchResultsUrl(), parameterQuery);

		return getJsonNthChildObject(jsonResponse, new String[]{
				"SearchResults:searchresults"
				,"response"
				,"results"
				,"result"
				,"zestimate"
			});
	}
	
	public JSONObject getDeepSearchResultsJson(final String zwsId, final String address, final String cityStateZip, final boolean rentZestimate){
		final String parameterQuery = buildGetDeepSearchResultsParameterQuery(zwsId, address, cityStateZip, rentZestimate);
		return getZillowJsonResponse(properties.deepSearchResultsUrl(), parameterQuery);
	}
	
	public JSONObject getDeepCompsJson(final String zwsId, final String zpid, final int count, final boolean rentZestimate){
		final String parameterQuery = buildGetDeepCompsParameterQuery(zwsId, zpid, count, rentZestimate);
		return getZillowJsonResponse(properties.deepCompsUrl(), parameterQuery);
	}

	protected JSONObject getZillowJsonResponse(final String baseURL, final String parameterQuery) {
		URLConnection connection = null;
		String responseString = null;
		JSONObject jsonObj = null;
		
		try {
			connection = new URL(baseURL + properties.urlParamSeparator() + parameterQuery).openConnection();
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
	
	protected JSONObject getJsonNthChildObject(JSONObject json, String[] childNodes){
		JSONObject returnObject = json;
		
		for(String childNode : childNodes){
			returnObject = (JSONObject) returnObject.get(childNode);
		}
		
		return returnObject;
	}

}
