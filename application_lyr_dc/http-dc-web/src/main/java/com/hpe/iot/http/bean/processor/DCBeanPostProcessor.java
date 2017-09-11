package com.hpe.iot.http.bean.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author sveera
 *
 */
@Component
public class DCBeanPostProcessor implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DCBeanPostProcessor.class);

	public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
		logger.info("Bean with id " + arg1 + " created with instance " + arg0);
		return arg0;
	}

	public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
		return arg0;
	}

}
