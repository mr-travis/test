package com.paic.client.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * 发送http请求到对应的web服务器上
 * 
 * @author EX_KJKFB_OUYANGH
 *
 */
public class HttpClient {

    /**
     * 
     */
	public static final String DEFAULT_MIME_TYPE = "text/plain";

	public byte[] request(byte[] requestData, String url) {
		RequestConfig config = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpPost post = null;
		byte[] responseData = new byte[0];
		Charset charset = null;
		// Http客户端对外调用开始

		long start = System.currentTimeMillis();
		try {
			List<Header> headers = new ArrayList<Header>();
			headers.add(new BasicHeader("Connection", "no"));
			config = RequestConfig.custom().setConnectionRequestTimeout(600000).setConnectTimeout(10000)
					.setSocketTimeout(500000).build();
			httpClient = HttpClients.custom().setDefaultRequestConfig(config).setRetryHandler(getRetryHandler(1))
					.setDefaultHeaders(headers).build();

			post = new HttpPost(url);
			post.setConfig(config);

			charset = Charset.forName("UTF-8");

			ContentType type = ContentType.create(DEFAULT_MIME_TYPE, charset);

			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestData, type);

			post.setEntity(byteArrayEntity);
			response = httpClient.execute(post);

			if (response == null) {
				throw new IllegalArgumentException(url);
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseData = EntityUtils.toByteArray(entity);
			} else {
				throw new IllegalArgumentException(url);
			}
			int statusCode = response.getStatusLine().getStatusCode();

			if (404 == statusCode) {
				throw new IllegalArgumentException(String.valueOf(statusCode) + ":" + url);
			}
			if (statusCode < 200 || statusCode > 299) {
				throw new IllegalArgumentException(String.valueOf(statusCode) + ":" + url);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(url, e);
		} finally {
			try {
				post.abort();
			} catch (Exception e) {
				// do nothing
			}
			if (response != null) {
				IOUtils.closeQuietly(response);
			}
			// 关闭连接管理器
			if (httpClient != null) {
				IOUtils.closeQuietly(httpClient);
			}
			System.out.println(String.valueOf((System.currentTimeMillis() - start)));
		}
		return responseData;
	}

	protected HttpRequestRetryHandler getRetryHandler(int retryTimes) {
		return new DefaultHttpRequestRetryHandler(retryTimes, false);
	}
}
