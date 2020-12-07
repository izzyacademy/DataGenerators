package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.utils.ApplicationConstants;

import java.sql.PreparedStatement;

public class OrderShipmentService extends BaseMicroService {

    private final int shipmentInterval;

    private final int shipmentCount;

    public OrderShipmentService()
    {
        this.shipmentInterval = ApplicationConstants.SHIPMENT_INTERVAL_SECONDS * 1000;
        this.shipmentCount = ApplicationConstants.SHIPMENT_COUNT;
    }

    @Override
    public void run() {

        try {

            while(true) {

                int itemsShipped = this.generateShipmentEvent();

                System.out.println();
                System.out.println(itemsShipped + " items have been shipped");
                System.out.println("Sleeping for " + shipmentInterval + " ms before next order shipment");
                System.out.println();

                // Wait for a bit, before creating the next order
                Thread.sleep(shipmentInterval);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private int generateShipmentEvent() {

        final String SQL_UPDATE = "UPDATE ecommerce.orders SET status='SHIPPED' WHERE status = 'FULFILLMENT' LIMIT ?";

        int totalItemsToBeShipped = this.shipmentCount;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE);

            preparedStatement.setInt(1, totalItemsToBeShipped);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}
