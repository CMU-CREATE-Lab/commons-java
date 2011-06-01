package edu.cmu.ri.createlab.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceProxyProvider<ProxyClass extends CreateLabDeviceProxy>
   {
   ProxyClass getCreateLabDeviceProxy();
   }