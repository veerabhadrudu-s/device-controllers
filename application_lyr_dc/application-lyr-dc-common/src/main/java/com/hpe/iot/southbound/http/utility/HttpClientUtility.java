/**
 * 
 */
package com.hpe.iot.southbound.http.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sveera
 *
 */
public class HttpClientUtility {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String getResourceOnHttp(String resourceURI) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(resourceURI);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return executeHttpUriRequest(httpClient, httpGet);
	}

	public String getResourceOnHttp(String resourceURI, Map<String, String> headers)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(resourceURI);
		logger.trace("Following headers" + headers + " are used to get resource  " + resourceURI);
		for (Map.Entry<String, String> header : headers.entrySet())
			httpGet.addHeader(header.getKey(), header.getValue());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return executeHttpUriRequest(httpClient, httpGet);
	}

	@Deprecated
	// Not working
	public String getResourceOnHttpUsingBasicAuth(String resourceURI, String username, String password)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(resourceURI);
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		provider.setCredentials(AuthScope.ANY, credentials);
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		return executeHttpUriRequest(httpClient, httpGet);
	}

	public String getResourceOnHttps(String resourceURI) throws KeyManagementException, NoSuchAlgorithmException,
			CertificateException, KeyStoreException, IOException {
		SSLContext sslContext = createTrustAnySSLContext();
		CloseableHttpClient httpClient = constructHttpClientForHttps(sslContext, false);
		HttpGet httpGet = new HttpGet(resourceURI);
		return executeHttpUriRequest(httpClient, httpGet);
	}

	public String getResourceOnHttps(String resourceURI, String certificatePath) throws KeyManagementException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
		return getResourceOnHttps(resourceURI, certificatePath, true);
	}

	public String getResourceOnHttps(String resourceURI, String certificatePath, boolean isHostNameVerificationRequired)
			throws KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException,
			IOException {
		SSLContext sslContext = createSSLContextFromCertificate(certificatePath);
		CloseableHttpClient httpClient = constructHttpClientForHttps(sslContext, isHostNameVerificationRequired);
		HttpGet httpGet = new HttpGet(resourceURI);
		return executeHttpUriRequest(httpClient, httpGet);
	}

	public String postRequestOnHttp(String resourceURI, Map<String, String> headers, String httpBody)
			throws ClientProtocolException, IOException {
		return postRequestOnHttp(resourceURI, headers, httpBody.getBytes());
	}

	public String postRequestOnHttp(String resourceURI, Map<String, String> headers, byte[] httpBody)
			throws ClientProtocolException, IOException {
		logger.trace("Following headers" + headers + " are used to post for resource  " + resourceURI);
		HttpPost httpPost = new HttpPost(resourceURI);
		for (Map.Entry<String, String> header : headers.entrySet())
			httpPost.addHeader(header.getKey(), header.getValue());
		HttpEntity httpEntity = new ByteArrayEntity(httpBody);
		httpPost.setEntity(httpEntity);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return executeHttpUriRequest(httpClient, httpPost);
	}

	public String postRequestOnHttps(String resourceURI, Map<String, String> headers, String httpBody)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException,
			CertificateException, KeyStoreException {
		logger.trace("Following headers" + headers + " are used to post for resource  " + resourceURI);
		SSLContext sslContext = createTrustAnySSLContext();
		CloseableHttpClient httpClient = constructHttpClientForHttps(sslContext, false);
		HttpPost httpPost = new HttpPost(resourceURI);
		for (Map.Entry<String, String> header : headers.entrySet())
			httpPost.addHeader(header.getKey(), header.getValue());
		HttpEntity httpEntity = new ByteArrayEntity(httpBody.getBytes());
		httpPost.setEntity(httpEntity);
		return executeHttpUriRequest(httpClient, httpPost);
	}

	public String postRequestOnHttps(String resourceURI, Map<String, String> headers, String httpBody,
			String certificatePath) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException {
		return postRequestOnHttps(resourceURI, headers, httpBody, certificatePath, true);
	}

	public String postRequestOnHttps(String resourceURI, Map<String, String> headers, String httpBody,
			String certificatePath, boolean isHostNameVerificationRequired) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		return postRequestOnHttps(resourceURI, headers, httpBody.getBytes(), certificatePath,
				isHostNameVerificationRequired);
	}

	public String postRequestOnHttps(String resourceURI, Map<String, String> headers, byte[] httpBody,
			String certificatePath, boolean isHostNameVerificationRequired) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		logger.trace("Following headers" + headers + " are used to post for resource  " + resourceURI);
		SSLContext sslContext = createSSLContextFromCertificate(certificatePath);
		CloseableHttpClient httpClient = constructHttpClientForHttps(sslContext, isHostNameVerificationRequired);
		HttpPost httpPost = new HttpPost(resourceURI);
		for (Map.Entry<String, String> header : headers.entrySet())
			httpPost.addHeader(header.getKey(), header.getValue());
		HttpEntity httpEntity = new ByteArrayEntity(httpBody);
		httpPost.setEntity(httpEntity);
		return executeHttpUriRequest(httpClient, httpPost);
	}

	private SSLContext createSSLContextFromCertificate(String certificatePath) throws NoSuchAlgorithmException,
			CertificateException, IOException, KeyStoreException, KeyManagementException {
		InputStream is = new FileInputStream(certificatePath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate caCert = (X509Certificate) cf.generateCertificate(is);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		// You don't need the KeyStore instance to come from a file.
		ks.load(null);
		ks.setCertificateEntry("caCert", caCert);
		tmf.init(ks);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);
		return sslContext;
	}

	private SSLContext createTrustAnySSLContext() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslContext = SSLContexts.custom().build();
		sslContext.init(null, new X509TrustManager[] { new DegenrateX509TrustManager() }, new SecureRandom());
		return sslContext;
	}

	private CloseableHttpClient constructHttpClientForHttps(SSLContext sslContext,
			boolean isHostNameVerificationRequired) {
		LayeredConnectionSocketFactory sslsf = isHostNameVerificationRequired
				? new SSLConnectionSocketFactory(sslContext)
				: new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();
		HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		return httpClient;
	}

	private String executeHttpUriRequest(CloseableHttpClient httpClient, HttpUriRequest httpUriRequest)
			throws IOException, ClientProtocolException {
		StringBuffer stringBuffer = new StringBuffer();
		try (CloseableHttpResponse response = httpClient.execute(httpUriRequest);
				InputStream inputStream = response.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String content;
			while ((content = bufferedReader.readLine()) != null) {
				stringBuffer.append(content);
			}
			logger.debug("Response received from resource URI - " + httpUriRequest.toString() + " is - "
					+ stringBuffer.toString() + " with status line " + response.getStatusLine());
		}
		httpClient.close();
		return stringBuffer.toString();
	}

}
