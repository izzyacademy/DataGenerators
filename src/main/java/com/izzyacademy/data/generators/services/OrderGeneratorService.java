package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.models.Customer;
import com.izzyacademy.data.generators.models.ProductInventoryLevel;
import com.izzyacademy.data.generators.utils.ApplicationConstants;
import com.izzyacademy.data.generators.utils.RandomUtil;
import org.apache.commons.compress.utils.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderGeneratorService extends BaseMicroService {

    private static final String INVENTORY_DATABASE = "inventory";

    private final List<String> orderSources = Lists.newArrayList();

    public OrderGeneratorService() {

        super(INVENTORY_DATABASE);

        orderSources.add("WEB");
        orderSources.add("MOBILE");
        orderSources.add("PHONE");
        orderSources.add("STORE");
    }

    @Override
    public void run() {

        try {

            while(true) {

                this.generateOrder();

                int minOrderInterval = ApplicationConstants.MIN_ORDER_INTERVAL_SECONDS;
                int maxOrderInterval = ApplicationConstants.MAX_ORDER_INTERVAL_SECONDS;

                // Number of seconds to wait before placing next order
                int orderIntervalSeconds = RandomUtil.getRandomNumber(minOrderInterval, maxOrderInterval);

                long orderInterval= orderIntervalSeconds * 1000;

                System.out.println("Sleeping for " + orderInterval + " ms before next order purchase");
                // Wait for a bit, before creating the next order
                Thread.sleep(orderInterval);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private void generateOrder() {

        List<Customer> customers = this.getCustomers();

        int selectedCustomerIndex = RandomUtil.getRandomNumber(0, customers.size());

        int orderSourceIndex = RandomUtil.getRandomNumber(0, orderSources.size());

        System.out.println("orderSourceIndex=" + orderSourceIndex + ", selectedCustomerIndex=" + selectedCustomerIndex);

        Customer customer = customers.get(selectedCustomerIndex);

        String orderSource = orderSources.get(orderSourceIndex);

        System.out.println("Customer=" + customer);
        System.out.println("orderSource=" + orderSource);

        List<ProductInventoryLevel> skuLevels = this.getInventoryLevels();

        boolean proceedToOrderPlacement = skuLevels.size() > 0;

        if (proceedToOrderPlacement) {

            // Create the Order Id

            int customerId = customer.getCustomerId();

            long orderId = this.createOrder(customerId, orderSource);

            // maximum number of items we can order
            int maxNumberOfOrderItems = RandomUtil.getRandomNumber(1, skuLevels.size() + 1);

            int startingIndex = RandomUtil.getRandomNumber(0, skuLevels.size());

            int endingIndex = Math.min(skuLevels.size() - 1, startingIndex + maxNumberOfOrderItems);

            List<ProductInventoryLevel> selectedSKUs1 = skuLevels.subList(startingIndex, endingIndex);

            List<ProductInventoryLevel> selectedSKUs = new ArrayList<>();

            if (0 == selectedSKUs1.size()) {

                selectedSKUs.add(skuLevels.get(0));

            } else {

                selectedSKUs.addAll(selectedSKUs1);
            }

            System.out.println("maxNumberOfOrderItems=" + maxNumberOfOrderItems);
            System.out.println("numberOfItems=" + selectedSKUs.size() + ", startingIndex=" +
                    startingIndex + ",endingIndex=" + endingIndex);

            for (ProductInventoryLevel slide: selectedSKUs) {

                int orderItemCount = RandomUtil.getRandomNumber(1, slide.getAvailableCount());

                int productId = slide.getProductId();

                String skuId = slide.getSkuId();

                // Add items to the order
                this.createOrderItems(orderId, customerId, productId, skuId, orderItemCount);
            }
        } else {

            System.out.println("No items available to order");
        }

    }

    private long createOrder(final int customerId, final String orderSource) {

        final String SQL_SELECT = "INSERT INTO ecommerce.orders (customer_id, order_source, date_created) VALUES " +
                " (?, ?, NOW())";

        long orderId = 0;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, orderSource);

            int result = preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {

                orderId = resultSet.getLong(1);

            }

            return orderId;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    private long createOrderItems(long orderId, int customerId, int productId, String skuId, int orderItemCount) {

        List<ProductInventoryLevel> skuInventoryLevels = this.getInventoryLevels();

        final String SQL_SELECT_ORDER_ITEMS = "INSERT INTO ecommerce.order_items (order_id, product_id, sku_id, " +
                "item_count, customer_id, date_created) VALUES " +
                " (?, ?, ?, ?, ?, NOW()) ";

        long orderLineItemId = Long.MAX_VALUE;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_ORDER_ITEMS, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setString(3, skuId);
            preparedStatement.setInt(4, orderItemCount);
            preparedStatement.setInt(5, customerId);

            int result = preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {

                orderLineItemId = resultSet.getInt(1);

            }

            String report = "CREATE ORDER ITEM (" + orderLineItemId + ") orderId=" + orderId +
                    ", productId=" + productId + ", sku=" + skuId + ", itemCount=" + orderItemCount;

            System.out.println(report);

            return orderLineItemId;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    protected List<Customer> getCustomers() {

        final List<Customer> customers = new ArrayList<>(32);

        final String SQL_SELECT = "SELECT customer_id, first_name, last_name, email " +
                "FROM ecommerce.customers";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                int customerId = resultSet.getInt("customer_id");
                String email = resultSet.getString("email");

                final Customer resultItem = new Customer(customerId, firstName, lastName, email);

                customers.add(resultItem);

            }

            return customers;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    /**
     * Returns the current inventory levels for each product SKU
     *
     * @return
     */
    protected List<ProductInventoryLevel> getInventoryLevels() {

        final List<ProductInventoryLevel> inventoryLevels = new ArrayList<>(32);

        final String SQL_SELECT = "SELECT sku_id, product_id, available_count, status " +
                "FROM inventory.product_inventory_levels " +
                "WHERE available_count > 0";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String skuId = resultSet.getString("sku_id");
                int productId = resultSet.getInt("product_id");
                int availableCount = resultSet.getInt("available_count");
                String status = resultSet.getString("status");

                final ProductInventoryLevel resultItem = new ProductInventoryLevel(skuId, productId,
                        availableCount, status);

                inventoryLevels.add(resultItem);

            }

            return inventoryLevels;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}
