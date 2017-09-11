package com.hpe.iot.mqtt.southbound.security;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

//@Component
public class SecurityLayer {

	private final boolean securityEnabled;
	private final String serverCertificate;
	private final String trustStorePath;
	private final String trustStorePassword;
	private final String keyStorePath;
	private final String keyStorePassword;
	private final ResourceLoader resourceLoader;

	@Autowired
	public SecurityLayer(@Value("${security.enabled}") boolean securityEnabled,
			@Value("${mqtt.broker.certificate.path}") String serverCertificate,
			@Value("${mqtt.truststore.path}") String trustStorePath,
			@Value("${mqtt.truststore.password}") String trustStorePassword,
			@Value("${mqtt.keystore.path}") String keyStorePath,
			@Value("${mqtt.keystore.password}") String keyStorePassword, ResourceLoader resourceLoader) {
		super();
		this.securityEnabled = securityEnabled;
		this.serverCertificate = serverCertificate;
		this.trustStorePath = trustStorePath;
		this.trustStorePassword = trustStorePassword;
		this.keyStorePath = keyStorePath;
		this.keyStorePassword = keyStorePassword;
		this.resourceLoader = resourceLoader;
	}

	public boolean isSecurityEnabled() {
		return securityEnabled;
	}

	private String getFullPath(String fileUrl) throws IOException {
		return resourceLoader.getResource(fileUrl).getURI().getPath();
	}

	public SSLSocketFactory getSSLSocketFactory() throws Exception {
		return TLSUtil.getStaticSocketFactory(getFullPath(trustStorePath), trustStorePassword,
				getFullPath(keyStorePath), keyStorePassword);
	}

}
