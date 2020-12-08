package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.utils.ApplicationConstants;

import java.sql.PreparedStatement;

public class OrderDeliveryService extends BaseMicroService {

    private final int deliveryInterval;

    private final int deliveryCount;

    public OrderDeliveryService()
    {
        this.deliveryInterval = ApplicationConstants.SHIPMENT_INTERVAL_SECONDS * 1000;
        this.deliveryCount = ApplicationConstants.SHIPMENT_COUNT;
    }

    @Override
    public void run() {

        try {

            while(true) {

                final int itemsDelivered = this.generateDeliveryEvent();

                System.out.println();
                System.out.println(itemsDelivered + " items have been delivered");
                System.out.println("Target Delivery Count=" + deliveryCount + " items");
                System.out.println("Sleeping for " + deliveryInterval + " ms before next order delivery");
                System.out.println();

                // Wait for a bit, before creating the next order
                Thread.sleep(deliveryInterval);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private int generateDeliveryEvent() {

        final String SQL_UPDATE = "UPDATE ecommerce.orders SET status='DELIVERED' WHERE status = 'SHIPPED' LIMIT ?";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE);

            preparedStatement.setInt(1, this.deliveryCount);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}
