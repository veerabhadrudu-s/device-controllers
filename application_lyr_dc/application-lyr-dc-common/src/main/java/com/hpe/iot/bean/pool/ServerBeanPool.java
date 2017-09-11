package com.hpe.iot.bean.pool;

/**
 * @author sveera
 *
 */
public interface ServerBeanPool {

	<T> T getBean(Class<T> classType);

}
