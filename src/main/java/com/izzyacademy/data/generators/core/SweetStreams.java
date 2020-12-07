package com.izzyacademy.data.generators.core;

import com.izzyacademy.data.generators.services.*;

import java.util.HashMap;
import java.util.Map;

// Run compiled application as
// java -jar .\target\streams-app-1.0.0-uber.jar
public class SweetStreams {

    private static final String DEFAULT_SERVICE = DefaultService.class.getSimpleName();

    public static void main(final String[] args) throws Exception {

        final Map<String, String> env = System.getenv();

        Map<String, String> serviceNames = new HashMap<>(8);

        serviceNames.put(DefaultService.class.getSimpleName(), DefaultService.class.getName());
        serviceNames.put(InventoryReplenishmentService.class.getSimpleName(), InventoryReplenishmentService.class.getName());
        serviceNames.put(OrderGeneratorService.class.getSimpleName(), OrderGeneratorService.class.getName());
        serviceNames.put(OrderShipmentService.class.getSimpleName(), OrderShipmentService.class.getName());
        serviceNames.put(OrderDeliveryService.class.getSimpleName(), OrderDeliveryService.class.getName());
        serviceNames.put(OrderReturnsService.class.getSimpleName(), OrderReturnsService.class.getName());

        final String serviceName = env.getOrDefault("SERVICE_NAME", DEFAULT_SERVICE);

        // Select one of the micro services listed above
        final String serviceClass = serviceNames.get(serviceName);

        System.out.println("Registered Micro Services");
        System.out.println(serviceNames.entrySet());
        System.out.println();
        System.out.println("serviceName=" + serviceName + ", serviceClass=" + serviceClass);
        System.out.println();

        // All classes must implement the KafkaStreamService interface
        DataGeneratorService service = (DataGeneratorService) Class.forName(serviceClass).newInstance();

        // Run the service
        service.run();
    }
}
