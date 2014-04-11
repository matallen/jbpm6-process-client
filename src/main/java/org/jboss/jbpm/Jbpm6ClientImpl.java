package org.jboss.jbpm;

import static com.jayway.restassured.RestAssured.given;
import static org.jboss.jbpm.Jbpm6ClientImpl.Http.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.http.HttpException;

import com.google.common.base.Preconditions;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class Jbpm6ClientImpl implements Jbpm6Client{
	private final String server;
	private final String username;
	private final String password;
	private final boolean debug;
	public enum Http{GET,POST}
	
	public Jbpm6ClientImpl(String serverUrl, String username, String password, boolean debug){
		Preconditions.checkArgument(serverUrl!=null);
		Preconditions.checkArgument(serverUrl.endsWith("/"), "the serverUrl must end with a / character");
		this.server=serverUrl;
		this.username=username;
		this.password=password;
		this.debug=debug;
	}
	
  public String getTasks(TasksBy by, String value) throws HttpException{
    return send(GET, "rest/task/query?"+by.name()+"="+value);
  }
	
	
	public String startProcess(String deploymentId, String processId, String mapOfParams) throws HttpException{
		return startProcessWithMap(deploymentId, processId, queryStringToMap(mapOfParams));
	}
	protected String startProcessWithMap(String deploymentId, String processId, Map<String,String> params) throws HttpException{
		Preconditions.checkArgument(deploymentId.split(":").length==3);
		return send(POST, "rest/runtime/"+deploymentId+"/process/"+processId+"/start"+mapToQueryString(params));
	}
	
	
	public String startTask(String id) throws HttpException{
		return send(POST, "rest/task/"+id+"/start");
	}
	
	
	public String completeTask(String id, String commaSeparatedListOfParams) throws HttpException{
		return completeTaskWithMap(id, queryStringToMap(commaSeparatedListOfParams));
	}
	public String completeTaskWithMap(String id, Map<String, String> params) throws HttpException{
		return send(POST, "rest/task/"+id+"/complete"+mapToQueryString(params));
	}
	
	private Response send(String url, Http httpType){
		RequestSpecification rs=
				given().redirects().follow(true)
				.auth().preemptive().basic(username,password)
				.when();
		Response response;
		switch (httpType){
			case POST:response = rs.post(server+url); break;
			case GET:response  = rs.get (server+url); break;
			default:response   = rs.get (server+url);
		}
		return response;
	}
	
	
	private String send(Http httpType, String url) throws HttpException{
		Response response=send(url, httpType);
		if (response.getStatusCode()!=200)
			throw new HttpException("Failed to "+httpType.name()+" to "+url+" - http status line = "+ response.getStatusLine() +"; response content = "+ response.asString());
		
		// add the status line for info/debugging purposes
		String result=response.asString();
		if (debug){
		  result=new StringBuffer(result)
  		.insert(result.indexOf(">")+1, "<!-- "+response.getStatusLine()+" -->")
  		.toString();
		}
		return result;
	}

	/**
	 * Convert a Map object into the jbpm6 queryString equivalent of a map of values but prepending the text "map_"
	 */
	private String mapToQueryString(Map<String,String> params){
		StringBuffer sb=new StringBuffer();
		for(Map.Entry<String, String> e:params.entrySet()){
			sb.append("&map_"+e.getKey()+"="+e.getValue());
		}
		if (sb.length()>0)sb.replace(0,1,"?");
		return sb.toString();
	}

	public static Map<String, String> queryStringToMap(String queryString) {
	    Map<String, String> result = new LinkedHashMap<String, String>();
	    String[] pairs = queryString.split(",");
	    for (String pair : pairs) {
	    	String[] keyValue=pair.split("=");
	    	if (keyValue.length==2){
	    		result.put(keyValue[0], keyValue[1]);
	    	}
//	        int idx = pair.indexOf("=");/
//	        result.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    return result;
	}

}
