package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.models.OrderLineItem;
import com.izzyacademy.data.generators.models.ProductInventoryLevel;
import com.izzyacademy.data.generators.utils.ApplicationConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderReturnsService extends BaseMicroService {

    private int orderReturnIntervalSeconds;

    private int orderItemReturnCount;

    private int orderItemAgeSeconds;

    public OrderReturnsService()
    {
        this.orderReturnIntervalSeconds = ApplicationConstants.ORDER_RETURN_INTERVAL_SECONDS * 1000;
        this.orderItemAgeSeconds = ApplicationConstants.ORDER_RETURN_AGE_SECONDS;
        this.orderItemReturnCount = ApplicationConstants.ORDER_RETURN_COUNT;
    }

    @Override
    public void run() {

        try {

            while(true) {

                generateReturnEvent();

                System.out.println();
                System.out.println("orderItemAgeSeconds=" + orderItemAgeSeconds + ", maxOrderItemReturnCount=" + orderItemReturnCount);
                System.out.println("Sleeping for " + orderReturnIntervalSeconds + " ms before next order item return");
                System.out.println();

                // Wait for a bit, before creating the next order
                Thread.sleep(orderReturnIntervalSeconds);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private void generateReturnEvent() {

        long itemsReturned = 0;
        int lineItemsUpdated = 0;

        List<OrderLineItem> returnCandidates = this.getOrderItemToBeReturned(orderItemAgeSeconds, orderItemReturnCount);

        for (OrderLineItem candidate: returnCandidates)  {

            lineItemsUpdated += this.updateOrderItem(candidate.getLineItemId(), "RETURNED");

            this.registerItemReturn(candidate.getProductId(), candidate.getLineItemId(),
                    candidate.getSkuId(), candidate.getItemCount());

            itemsReturned += candidate.getItemCount();
        }

        int cancelledOrders = this.cancelOrdersWithOnlyReturnedItems();

        System.out.println("lineItemsUpdated=" + lineItemsUpdated + ", itemsReturned=" +
                itemsReturned + ", cancelledOrders=" + cancelledOrders);
    }

    protected int cancelOrdersWithOnlyReturnedItems() {

        final String SQL_UPDATE = "UPDATE ecommerce.orders a " +
                "INNER JOIN ecommerce.order_items b ON (a.order_id = b.order_id) " +
                "SET a.status = 'CANCELLED' " +
                "WHERE a.status = 'DELIVERED' AND " +
                "a.order_id NOT IN (SELECT DISTINCT order_id FROM  ecommerce.order_items WHERE status = 'ORDERED') ";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    private long registerItemReturn(int productId, long orderLineItemId, String skuId, int orderItemCount) {

        final String SQL_INSERT = "INSERT INTO inventory.order_returns " +
                "SET product_id = ?, " +
                "order_line_item_id = ?, " +
                "sku_id = ?, " +
                "return_count = ?, " +
                "date_created = NOW() ";

        long recordId = Long.MAX_VALUE;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, productId);
            preparedStatement.setLong(2, orderItemCount);
            preparedStatement.setString(3, skuId);
            preparedStatement.setInt(4, orderItemCount);

            int result = preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {

                recordId = resultSet.getInt(1);

            }

            return recordId;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    protected int updateOrderItem(long itemId, String status) {

        final String SQL_UPDATE = "UPDATE ecommerce.order_items a SET status = ? WHERE a.order_line_item_id = ? LIMIT 1";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE);

            preparedStatement.setString(1, status);
            preparedStatement.setLong(2, itemId);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    protected List<OrderLineItem> getOrderItemToBeReturned(int returnAgeSeconds, int itemReturnCount) {

        List<OrderLineItem> orderLineItems = new ArrayList<>(32);

        final String SQL_SELECT = "SELECT a.* FROM ecommerce.order_items a " +
                "INNER JOIN ecommerce.orders b ON (a.order_id = b.order_id) " +
                "WHERE b.date_modified > DATE_SUB(NOW(), INTERVAL ? SECOND) AND b.status = 'DELIVERED' " +
                "LIMIT ?";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);

            preparedStatement.setInt(1, returnAgeSeconds);
            preparedStatement.setInt(2, itemReturnCount);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Long itemId = resultSet.getLong("order_line_item_id");
                int itemCount = resultSet.getInt("item_count");
                String skuId = resultSet.getString("sku_id");
                int productId = resultSet.getInt("product_id");
                int orderId = resultSet.getInt("order_id");
                int customerId = resultSet.getInt("customer_id");
                String status = resultSet.getString("status");

                final OrderLineItem resultItem = new OrderLineItem(
                        itemId, itemCount, skuId, productId, orderId, customerId, status);

                orderLineItems.add(resultItem);

            }

            return orderLineItems;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}