package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.models.Customer;
import com.izzyacademy.data.generators.models.ProductInventoryLevel;
import com.izzyacademy.data.generators.utils.MySQLUtil;
import com.izzyacademy.data.generators.utils.RandomUtil;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderGeneratorService implements DataGeneratorService, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(OrderGeneratorService.class);

    /**
     * Number of Seconds to Wait before next event
     */
    public static final String DEFAULT_INTERVAL = "3";

    private static final String INVENTORY_DATABASE = "inventory";

    private long orderInterval = 1000;

    private final List<String> orderSources = Lists.newArrayList();

    private MySQLUtil databaseUtil;

    private Connection conn;

    public OrderGeneratorService() {

        final Map<String, String> env = System.getenv();

        final String orderIntervalValue = env.getOrDefault("ORDER_INTERVAL", DEFAULT_INTERVAL);

        this.orderInterval = Long.parseLong(orderIntervalValue) * 1000;

        this.databaseUtil = new MySQLUtil();

        this.conn = this.databaseUtil.getConnection(INVENTORY_DATABASE);

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

                logger.info("Sleeping for " + orderInterval + " ms before next order purchase");
                // Wait for a bit, before creating the next order
                Thread.sleep(orderInterval);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private void generateOrder() {

        boolean proceedToOrderPlacement = true;

        List<Customer> customers = this.getCustomers();

        int selectedCustomerIndex = RandomUtil.getRandomNumber(0, customers.size());

        int orderSourceIndex = RandomUtil.getRandomNumber(0, orderSources.size());

        System.out.println("orderSourceIndex=" + orderSourceIndex + ", selectedCustomerIndex=" + selectedCustomerIndex);

        Customer customer = customers.get(selectedCustomerIndex);

        String orderSource = orderSources.get(orderSourceIndex);

        System.out.println("Customer=" + customer);
        System.out.println("orderSource=" + orderSource);

        if (proceedToOrderPlacement) {
            // Create the Order Id
            int orderId = this.createOrder(customer.getCustomerId(), orderSource);

            List<ProductInventoryLevel> skuLevels = this.getInventoryLevels();

            int numberOfOrderItems = RandomUtil.getRandomNumber(1, skuLevels.size() + 1);

            List<ProductInventoryLevel> skuSlice = skuLevels.subList(0, numberOfOrderItems);

            for (ProductInventoryLevel slide: skuSlice) {

                int orderItemCount = RandomUtil.getRandomNumber(1, slide.getAvailableCount());

                int productId = slide.getProductId();

                String skuId = slide.getSkuId();

                // Add items to the order
                this.createOrderItems(orderId, productId, skuId, orderItemCount);
            }
        }

        System.out.println("\n");
    }

    private int createOrder(final int customerId, final String orderSource) {

        final String SQL_SELECT = "INSERT INTO ecommerce.orders (customer_id, order_source, date_created) VALUES " +
                " (?, ?, NOW())";

        int orderId = 0;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, orderSource);

            int result = preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {

                orderId = resultSet.getInt(1);

            }

            return orderId;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    private int createOrderItems(final int orderId, int productId, String skuId, int orderItemCount) {

        List<ProductInventoryLevel> skuInventoryLevels = this.getInventoryLevels();

        final String SQL_SELECT_ORDER_ITEMS = "INSERT INTO ecommerce.order_items (order_id, product_id, sku_id, " +
                "item_count, date_created) VALUES " +
                " (?, ?, ?, ?, NOW()) ";

        int orderLineItemId = Integer.MAX_VALUE;

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_ORDER_ITEMS, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setString(3, skuId);
            preparedStatement.setInt(4, orderItemCount);

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

                final Customer resultItem = new Customer(customerId, firstName,
                        lastName, email);

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
                "FROM inventory.product_inventory_levels";

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

    @Override
    public void close() throws Exception {

        this.databaseUtil.close();
    }
}
