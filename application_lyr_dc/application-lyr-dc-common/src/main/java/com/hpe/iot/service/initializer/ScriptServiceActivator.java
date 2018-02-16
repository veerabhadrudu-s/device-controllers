/**
 * 
 */
package com.hpe.iot.service.initializer;

/**
 * @author sveera
 *
 */
public interface ScriptServiceActivator extends ServiceActivator {

	public void startService(String scriptFullPath);

	public void stopService(String scriptFullPath);

}
