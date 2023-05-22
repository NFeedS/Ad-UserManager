package com.nfeeds.adapter.usermanager.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpUtils {
	
	public static HttpResponse<String> getRequest(URI uri) throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(uri)
				.GET()
				.version(HttpClient.Version.HTTP_2)
				.header("Content-Type", "application/json")
				.build();
		
		return HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
	}
	
	public static HttpResponse<String> postRequest(URI uri, String body) throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(uri)
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.version(HttpClient.Version.HTTP_2)
				.header("Content-Type", "application/json")
				.build();
		
		return HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
	}
	
	public static HttpResponse<String> putRequest(URI uri, String body) throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(uri)
				.PUT(HttpRequest.BodyPublishers.ofString(body))
				.version(HttpClient.Version.HTTP_2)
				.header("Content-Type", "application/json")
				.build();
		
		return HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
	}
	
	public static HttpResponse<String> deleteRequest(URI uri) throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(uri)
				.DELETE()
				.version(HttpClient.Version.HTTP_2)
				.header("Content-Type", "application/json")
				.build();
		
		return HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
	}
}
