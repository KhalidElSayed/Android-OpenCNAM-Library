package com.tomdignan.android.opencnam.library;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/**
 * Reusable OpenCNAM request. Can return result in XML, JSON, or TEXT formats.
 * 
 * @author Tom Dignan
 */
public class OpenCNAMRequest implements Request {
	//=========================================================================
	// Static Members
	//=========================================================================
	
	/** Tag for identifying class in Log */
	private static final String TAG = "OpenCNAMRequest";
	
	/** Base URL for request. MUST include trailing slash. */
	private static final String OPENCNAM_BASE_URL = "https://api.opencnam.com/v1/phone/";
	
	/** Identifier for api_key parameter */
	private static final String PARAM_API_KEY = "api_key";
	
	/** Identifier for format parameter */
	private static final String PARAM_FORMAT = "format";
	
	/** Identifier for username parameter */
	private static final String PARAM_USERNAME = "username";
	
	/** Format value for XML */
	public static final String FORMAT_XML = "xml";
	
	/** Format value for JSON */
	public static final String FORMAT_JSON = "json";
	
	/** Format values for plain text */
	public static final String FORMAT_TEXT = "text";
	
	
	//=========================================================================
	// Instance Members
	//==============================================================javadoc===========
	
	/** Phone number to look up CNAM */
	private String mPhoneNumber = null;
	
	/** Serialization format for response. Default is TEXT. */
	private String mSerializationFormat = FORMAT_TEXT;
	
	/** Optional username parameter */
	private String mUsername = null;
	
	/** Optional API key */
	private String mAPIKey = null;
	
	/** Reusable HttpClient instance */
	private HttpClient mHttpClient = new DefaultHttpClient();
	
	/** Reusable HttpPost instance */
	private HttpGet mHttpGet = new HttpGet();
	
	/** Reusable ResponseHandler instance */
	private BasicResponseHandler mResponseHandler = new BasicResponseHandler();
	
	//=========================================================================
	// Constructors
	//=========================================================================
	
	/** Default constructor */
	public OpenCNAMRequest() {
		// NOOP
	}
	
	/** 
	 * Convenience constructor to pre-set phone number.
	 * 
	 * @param phoneNumber
	 */
	public OpenCNAMRequest(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	/** 
	 * Initialize all fields on creation with this constructor if desired.
	 * 
	 * @param mPhoneNumber
	 * @param mSerializationFormat
	 * @param mUsername
	 * @param mAPIKey
	 */
	public OpenCNAMRequest(String phoneNumber, String serializationFormat,
			String username, String apiKey) {
		mPhoneNumber = phoneNumber;
		mSerializationFormat = serializationFormat;
		mUsername = username;
		mAPIKey = apiKey;
	}


	//=========================================================================
	// Accessors
	//=========================================================================
	
	/**
	 * Set the optional username parameter 
	 */
	public void setUsername(String username) {
		mUsername = username;
	}
	
	/** 
	 * Set the API key.
	 * @param apiKey
	 */
	public void setAPIKey(String apiKey) {
		mAPIKey = apiKey;
	}

	/**
	 * Set the phone number to be looked up when execute() is called.
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	/**
	 * Sets the serialization format for the response. T
	 * 
	 * @param format -- must be one of the FORMAT_* constants defined in this class.
	 */
	public void setSerializationFormat(String format) {
		if (format != null && 
				(  format.equals(FORMAT_JSON) 
				|| format.equals(FORMAT_TEXT)
				|| format.equals(FORMAT_XML)) ) {
			mSerializationFormat = format;
		} else {
			throw new IllegalArgumentException("Invalid format. Must be one of the FORMAT_* constants.");
		}
	}
	
	//=========================================================================
	// Request Interface
	//=========================================================================
	
	/**
	 * Returns the serialized version of the desired output format on success, 
	 * or throws an exception on failure. You will need to deserialize this 
	 * result depending on the format you chose.
	 * 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @returns response
	 */
	@Override
	public String execute() throws ClientProtocolException, IOException {
		StringBuilder uriBuilder = new StringBuilder();
		mHttpGet.setURI(makeRequestURI());
		Log.d(TAG, "requestLine="+mHttpGet.getRequestLine());
		Log.d(TAG, "params="+mHttpGet.getParams().toString());
		HttpResponse response = mHttpClient.execute(mHttpGet);
		// It is slower to convert the response to a string first, but so much easier to
		// debug and such a negligible speed consideration when the payload is this small
		// that it is the best way.
		return mResponseHandler.handleResponse(response);
	}	
	
	//=========================================================================
	// Private Helpers
	//=========================================================================
	
	/** 
	 * Build out an appropriate URI for this request.
	 * 
	 * @return params
	 */
	private URI makeRequestURI() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(OPENCNAM_BASE_URL);
		builder.append(mPhoneNumber);
		
		builder.append("?");
		builder.append(PARAM_FORMAT);
		builder.append("=");
		builder.append(mSerializationFormat);
		
		if (mAPIKey != null) {
			builder.append("&");
			builder.append(PARAM_API_KEY);
			builder.append("=");
			builder.append(mAPIKey);
		}
		
		if (mUsername != null) {
			builder.append("&");
			builder.append(PARAM_USERNAME);
			builder.append("=");
			builder.append(mUsername);
		}

		return URI.create(builder.toString());
	}
}