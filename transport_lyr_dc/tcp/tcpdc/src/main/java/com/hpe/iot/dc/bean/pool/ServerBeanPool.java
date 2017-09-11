package com.hpe.iot.dc.bean.pool;

/**
 * @author sveera
 *
 */
public interface ServerBeanPool {

	<T> T getBean(Class<T> classType);

}
