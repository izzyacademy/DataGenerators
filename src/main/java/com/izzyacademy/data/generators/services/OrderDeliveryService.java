package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.utils.ApplicationConstants;

import java.sql.PreparedStatement;

public class OrderDeliveryService extends BaseMicroService {

    private final int shipmentInterval;

    private final int shipmentCount;

    public OrderDeliveryService()
    {
        this.shipmentInterval = ApplicationConstants.SHIPMENT_INTERVAL_SECONDS * 1000;
        this.shipmentCount = ApplicationConstants.SHIPMENT_COUNT;
    }

    @Override
    public void run() {

        try {

            while(true) {

                int itemsShipped = this.generateDeliveryEvent();

                System.out.println();
                System.out.println(itemsShipped + " items have been delivered");
                System.out.println("Sleeping for " + shipmentInterval + " ms before next order delivery");
                System.out.println();

                // Wait for a bit, before creating the next order
                Thread.sleep(shipmentInterval);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private int generateDeliveryEvent() {

        final String SQL_UPDATE = "UPDATE ecommerce.orders SET status='DELIVERED' WHERE status = 'SHIPPED' LIMIT ?";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE);

            preparedStatement.setInt(1, this.shipmentCount);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}
