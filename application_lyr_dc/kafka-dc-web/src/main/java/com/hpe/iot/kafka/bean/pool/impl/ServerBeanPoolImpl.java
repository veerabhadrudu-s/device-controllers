package com.hpe.iot.kafka.bean.pool.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hpe.iot.bean.pool.ServerBeanPool;

/**
 * @author sveera
 *
 */
public class ServerBeanPoolImpl implements ServerBeanPool, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public <T> T getBean(Class<T> classType) {
		return applicationContext.getBean(classType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
