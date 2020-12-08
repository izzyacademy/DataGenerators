package com.izzyacademy.data.generators.core;

import com.izzyacademy.data.generators.services.*;

import java.util.HashMap;
import java.util.Map;

// Run compiled application as
// java -jar .\target\*-uber.jar
public class SweetStreams {

    private static final String DEFAULT_SERVICE = DefaultService.class.getSimpleName();

    private static final Map<String, String> serviceNames = new HashMap<>(8);

    static {

        // Register the Micro Services
        registerMicroService(DefaultService.class);
        registerMicroService(InventoryReplenishmentService.class);
        registerMicroService(OrderGeneratorService.class);
        registerMicroService(OrderShipmentService.class);
        registerMicroService(OrderDeliveryService.class);
        registerMicroService(OrderReturnsService.class);
    }

    /**
     * Registers the MicroService in the registry of service names
     *
     * @param microServiceClass Micro Service Class
     */
    private static void registerMicroService(Class<? extends DataGeneratorService> microServiceClass) {
        serviceNames.put(microServiceClass.getSimpleName(), microServiceClass.getName());
    }

    public static void main(final String[] args) throws Exception {

        // Retrieving the environment variables
        final Map<String, String> env = System.getenv();

        // Picking the service name from the environment variable
        final String serviceName = env.getOrDefault("SERVICE_NAME", DEFAULT_SERVICE);

        // Select one of the registered micro services
        final String serviceClass = serviceNames.get(serviceName);

        System.out.println("Registered Micro Services");
        System.out.println(serviceNames.entrySet());
        System.out.println();
        System.out.println("selectedServiceName=" + serviceName + ", serviceClass=" + serviceClass);
        System.out.println();

        // All classes must implement the DataGeneratorService interface
        DataGeneratorService service = (DataGeneratorService) Class.forName(serviceClass).newInstance();

        // Run the service
        service.run();
    }
}
