package com.hpe.iot.dc.tcp.bean.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author sveera
 *
 */
@Component
public class DCBeanPostProcessor implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DCBeanPostProcessor.class);

	public Object postProcessAfterInitialization(Object arg0, String arg1) {
		logger.info(String.format("Bean with id %s created with instance %s", arg1, arg0));
		return arg0;
	}

	public Object postProcessBeforeInitialization(Object arg0, String arg1) {
		return arg0;
	}

}
