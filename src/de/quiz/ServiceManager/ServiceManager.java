package de.quiz.ServiceManager;

import java.util.HashMap;
import java.util.Map;


import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.LoggingManager.LoggingManager;


/**
 * This singleton class manages all services/managers. If anyone needs to get a 
 * service he has to call this class. It registers important services by default!
 * 
 * @author Patrick Na§
 */
public class ServiceManager {

    // variables
    private static ServiceManager instance; // Singleton!
    private Map<String, IService> serviceMap = new HashMap<String, IService>();

    /**
     * singleton stuff...
     */
    protected ServiceManager() {

	// CAUTION! Must be empty, otherwise singleton won't work!
    }
    public static synchronized ServiceManager getInstance() {

	// retrun only this one instance!
	if (instance == null) {
	    
	    instance = new ServiceManager();
	    instance.init();
	}
	return instance;
    }

    /**
     * singleton "constructor": register some default services in a specific order!
     */
    public void init() {
	try {

	    // register default services
	    registerService(ILoggingManager.class.getSimpleName(), new LoggingManager());

	    

	    ServiceManager.getInstance().getService(ILoggingManager.class).log(this, "sucessfully started default services");

	} catch (Exception e) {

	    throw new RuntimeException("Service Manager: unable to start default services! Reason " + e.toString());
	}
    }

    /**
     * register further services with the service manager
     * 
     * @param interfaceName	the class type of the service interface
     * @param service		the service itself
     */
    public void registerService(String interfaceName, IService service) {

	// only register if we have valid parameters
	if ((interfaceName != null) && (service != null)) {

	    // FIXME: check that we can only register one service of a type
	    serviceMap.put(interfaceName, service);

	} else {

	    throw new RuntimeException("Service Manager: unable to register service " + interfaceName);
	}
    }

    /**
     * use this method to get a specific service by it's interface
     * 
     * @param serviceInterface	the interface of the service we want to get
     * @return 			the requested service
     */
    public <T> T getService(Class<T> serviceInterface) {

	// check if key exists
	if (serviceMap.containsKey(serviceInterface.getSimpleName())) {

	    // check if service reference is not null (should never happen)
	    if (serviceMap.get(serviceInterface.getSimpleName()) != null) {

		// cast and return service
		return serviceInterface.cast(serviceMap.get(serviceInterface.getSimpleName()));
	    }
	}

	throw new RuntimeException("Service Manager: unable to get requested service " + serviceInterface.getSimpleName());
    }
}