package com.xzy.cm.common.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;

/**
 * 临时应急用，后续都转向到RestTemplate
 * 
 */
public class HttpHelper {

	// public final static String FAKE_HTTPS = "fakes";
	// public final static String SELF_HTTPS = "selfs";

	public final static ContentType CONTENT_TYPE_FORM = ContentType.create(URLEncodedUtils.CONTENT_TYPE, "UTF-8");
	public final static ContentType CONTENT_TYPE_TEXT = ContentType.create("text/plain", "UTF-8");
	public final static ContentType CONTENT_TYPE_JSON = ContentType.create("application/json", "UTF-8");
	private final static PoolingHttpClientConnectionManager cm;
	private final static RequestConfig config;
	static {
		cm = new PoolingHttpClientConnectionManager();
		// 将最大连接数增加到200
		cm.setMaxTotal(200);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(20);
		//
		config = RequestConfig.custom().setConnectTimeout('\uea60').setSocketTimeout(60000).build();
	}

	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	public static String stringify(Object s) {
		if (s == null) {
			return "";
		} else if (s.getClass().isPrimitive()) {
			return String.valueOf(s);
		} else if (s instanceof String) {
			return (String) s;
		} else {
			return JSON.toJSONString(s);
		}
	}

	/**
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String buildGetURL(String url, JSONObject param) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : param.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			if (value.getClass().isArray()) {
				for (Object v : (Object[]) value) {
					if (sb.length() > 0) {
						sb.append("&");
					}
					sb.append(key).append("=").append(stringify(v));
				}
			} else if(value instanceof JSONObject){
				if (sb.length() > 0) {
					sb.append("&");
				}
				String c = encode(stringify(value)).replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%26", "&").replaceAll("%3D", "=").replaceAll("%3F", "?");
				sb.append(key).append("=").append(c);
			}else {
				if (sb.length() > 0) {
					sb.append("&");
				}
				
				sb.append(key).append("=").append(encode(stringify(value)));
			}
		}
		if (url.indexOf("?") == -1) {// 还没有分割符号的
			return url + "?" + sb.toString();
		} else {
			return url + sb.toString();
		}
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
			String tmp = buildGetURL(url, param);
			return getContent(tmp, "", http_method, contentType, headers);
		}
		return getContent(url, param.toJSONString(), http_method, contentType, headers);
	}

	public static String getContent(String url, String content, String http_method, ContentType contentType, Header... headers) throws Exception {
		String body = null;

		CloseableHttpClient client = null;
		if (url.startsWith("http")) {
			// client = HttpClientBuilder.create().build();
			client = HttpClients.custom().setDefaultRequestConfig(config).setConnectionManager(cm).setConnectionManagerShared(true).build();
		}
		if (client == null) {
			client = HttpClients.custom().setDefaultRequestConfig(config).setConnectionManager(Self.getConnectionManager()).setConnectionManagerShared(true)
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
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				body = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				body = "http status:" + statusCode;
				response.close();
			}
		} catch (IOException e) {
			// return ExceptionUtils.getStackTrace(e);
			throw e;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (client != null) {
				try {
					// client.close();
				} catch (Exception e2) {
					// ignore first soso
				}

			}
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

		public static HttpClientConnectionManager getConnectionManager() {
			SSLContext sslcontext = getSSLContext();
			HostnameVerifier hostnameVerifier = getHostnameVerifier();
			//
			RegistryBuilder<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create();
			PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);
			r = r.register("https", sslsf);
			r = r.register("http", plainsf);
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(r.build());
			connectionManager.setMaxTotal(100);
			return connectionManager;
			//
		}
	}

}
