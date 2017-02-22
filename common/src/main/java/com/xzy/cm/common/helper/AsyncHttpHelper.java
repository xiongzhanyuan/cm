package com.xzy.cm.common.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Future;

/**
 * 临时应急用，后续都转向到RestTemplate
 * 
 */
public class AsyncHttpHelper {
	/**
	 * 日志
	 */
	private final static Logger log = Logger.getLogger("SERVICE");

	public final static ContentType CONTENT_TYPE_FORM = ContentType.create(URLEncodedUtils.CONTENT_TYPE, "UTF-8");
	public final static ContentType CONTENT_TYPE_TEXT = ContentType.create("text/plain", "UTF-8");
	public final static ContentType CONTENT_TYPE_JSON = ContentType.create("application/json", "UTF-8");

//	private final static ;
	private final static IOReactorConfig config;
	private final static RequestConfig requestConfig;

	static {

		config = IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors())
				.setConnectTimeout(30000).setSoTimeout(30000).build();
		requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
	}

	public static String getContent(String url) throws Exception {
		return getContent(url, (String) null, null, (Header[]) null);
	}

	public static String getContent(String url, Header... headers) throws Exception {
		return getContent(url, (String) null, null, headers);
	}

	public static String getContent(String url, String content) throws Exception {
		return getContent(url, content, CONTENT_TYPE_TEXT, (Header[]) null);
	}

	public static String getContent(String url, String content, Header... headers) throws Exception {
		return getContent(url, content, CONTENT_TYPE_TEXT, headers);
	}

	public static String getContent(String url, String content, ContentType contentType) throws Exception {
		return getContent(url, content, contentType, (Header[]) null);
	}

	/**
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String post(String url, Object param) throws Exception {
		return getContent(url, JSON.toJSONString(param), "post", ContentType.APPLICATION_JSON);
	}

	/**
	 *设置header的post
	 * @param url
	 * @param param
	 * @return
	 */
	public static String post(String url, Object param,Header... headers) throws Exception {
		return getContent(url, JSON.toJSONString(param), "post", ContentType.APPLICATION_JSON, headers);
	}

	public static String getContent(String url, JSONObject param, String http_method, ContentType contentType, Header... headers) throws Exception {
		// 如果是get模式，参数还不为空，那就搞一下
		if ("get".equals(http_method) && null != param) {
			String tmp = HttpHelper.buildGetURL(url, param);
			return getContent(tmp, "", http_method, contentType, headers);
		}
		return getContent(url, param.toJSONString(), http_method, contentType, headers);
	}

	public static String getContent(String url, String content, String http_method, ContentType contentType, Header... headers) throws Exception {
		String body = null;
		CloseableHttpAsyncClient client = null;

		ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(config);

		PoolingNHttpClientConnectionManager ncm = new PoolingNHttpClientConnectionManager(ioReactor);
		ncm.setMaxTotal(200);
		ncm.setDefaultMaxPerRoute(20);
//		httpclient.start();
		if (url.startsWith("http")) {
			client = HttpAsyncClients.custom().setConnectionManager(ncm).setDefaultRequestConfig(requestConfig).build();
		}

		if (client == null) {
			client = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(Self.getConnectionManager()).setConnectionManagerShared(true)
					.build();
		}

		HttpUriRequest request = null;
		if (StringUtils.isBlank(http_method)) {
			if (content != null) {
				request = new HttpPost(url);
				if (content.length() > 0) {
					((HttpPost) request).setEntity(new StringEntity(content, contentType));
				}
			} else {
				request = new HttpGet(url);
			}
		} else {
			if ("get".equals(http_method)) {
				request = new HttpGet(url);
			} else if ("post".equals(http_method)) {
				request = new HttpPost(url);
				if (StringUtils.isNotBlank(content)) {
					((HttpPost) request).setEntity(new StringEntity(content, contentType));
				}
			} else if ("delete".equals(http_method)) {
				request = new HttpDelete(url);
			}
		}

		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
		HttpClientContext localContext = HttpClientContext.create();
		if (!client.isRunning()) {
			client.start();
		}
		Future<HttpResponse> future = client.execute(request, localContext, null);
		HttpResponse response = future.get();
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				body = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				body = "http status:" + statusCode;
			}
		} catch (IOException e) {
			throw e;
		} finally {
			client.close();
		}

		return body;
	}

	public static String getContent(String url, String content, ContentType contentType, Header... headers) throws Exception {
		return getContent(url, content, null, contentType, headers);
	}

	private static class Self {

		private static X509TrustManager getTrustManager() {
			return new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// return null;
					return new X509Certificate[] {};
				}
			};
		}

		/**
		 * private static X509HostnameVerifier getHostnameVerifier() { return
		 * new X509HostnameVerifier() {
		 * 
		 * @Override public boolean verify(String arg0, SSLSession arg1) {
		 *           return true; }
		 * 
		 * @Override public void verify(String host, SSLSocket ssl) throws
		 *           IOException { }
		 * 
		 * @Override public void verify(String host, X509Certificate cert)
		 *           throws SSLException { }
		 * 
		 * @Override public void verify(String host, String[] cns, String[]
		 *           subjectAlts) throws SSLException { } }; }
		 */
		private static HostnameVerifier getHostnameVerifier() {
			return new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
		}

		private static SSLContext getSSLContext() {
			SSLContext context = null;
			X509TrustManager trustManager = getTrustManager();
			try {
				context = SSLContext.getInstance("SSL");
				context.init(null, new TrustManager[] { trustManager }, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				// do nothing
			} catch (KeyManagementException e) {
				// do nothing
			}
			return context;
		}

		public static NHttpClientConnectionManager getConnectionManager() throws IOReactorException {
			SSLContext sslcontext = getSSLContext();
			HostnameVerifier hostnameVerifier = getHostnameVerifier();
			//
			Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
					.register("http", NoopIOSessionStrategy.INSTANCE)
					.register("https", new SSLIOSessionStrategy(sslcontext, hostnameVerifier)).build();
			ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(config);
			PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor, sessionStrategyRegistry);
			connectionManager.setMaxTotal(100);
			return connectionManager;
			//
		}
	}

}
