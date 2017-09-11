
package com.hpe.iot.dc.iot.sdk.dependancy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @author sveera
 *
 */
@Configuration
public class RestConfigurations {
	
	@Bean(name = "restTemplate")
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate=new RestTemplate();  
        return restTemplate;
    }   
	
	public HttpComponentsClientHttpRequestFactory getHttpClientFactory(){
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
		return httpClientFactory;	
	}

}
