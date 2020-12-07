package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.models.ProductReplenishmentAnalysis;
import com.izzyacademy.data.generators.utils.ApplicationConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InventoryReplenishmentService extends BaseMicroService {

    private final int replenishmentIntervalSeconds;

    public InventoryReplenishmentService() {
        this.replenishmentIntervalSeconds = ApplicationConstants.REPLENISHMENT_INTERVAL_SECONDS * 1000;
    }

    @Override
    public void run() {

        try {

            while (true) {

                int itemsReplenished = this.generateReplenishmentEvent();

                System.out.println();
                System.out.println(itemsReplenished + " items have been replenished");
                System.out.println("Sleeping for " + replenishmentIntervalSeconds + " ms before next replenishment");
                System.out.println();
                System.out.println();
                System.out.println();

                // Wait for a bit, before creating the next order
                Thread.sleep(replenishmentIntervalSeconds);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private int generateReplenishmentEvent() {

        List<ProductReplenishmentAnalysis> replenishmentAnalyses = this.getReplenishmentAnalyses();

        int skusReplenished=replenishmentAnalyses.size();
        int totalItemsReplenished=0;

        for (ProductReplenishmentAnalysis replenishmentAnalysis: replenishmentAnalyses) {

            int productId = replenishmentAnalysis.getProductId();
            String skuId = replenishmentAnalysis.getSkuId();
            int replenishmentCount = replenishmentAnalysis.getReplenishmentCount();
            int hwm = replenishmentAnalysis.getHighWaterMark();
            int lwm = replenishmentAnalysis.getLowWaterMark();
            int availableCount = replenishmentAnalysis.getAvailableCount();

            this.registerReplenishment(productId, skuId, replenishmentCount);

            totalItemsReplenished += replenishmentCount;

            System.out.println("productId=" + productId + ", skuId=" + skuId + ", replenishmentCount" +
                    replenishmentCount + ", lwm=" + lwm + ", hwm=" + hwm + ", availableCount=" + availableCount);
        }

        System.out.println("skusReplenished=" + skusReplenished + ", totalItemsReplenished=" + totalItemsReplenished);

        return totalItemsReplenished;
    }

    private int registerReplenishment(int productId, String skuId, int replenishmentCount) {

        final String SQL_INSERT = "INSERT INTO inventory.replenishments " +
                "SET product_id = ?, sku_id = ?, replenishment_count = ?, date_created = NOW()";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT);

            preparedStatement.setInt(1, productId);
            preparedStatement.setString(2, skuId);
            preparedStatement.setInt(3, replenishmentCount);

            int result = preparedStatement.executeUpdate();

            int updateCount = preparedStatement.getUpdateCount();

            return updateCount;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }

    /**
     * Returns the current replenishment levels for each product SKU
     *
     * @return
     */
    protected List<ProductReplenishmentAnalysis> getReplenishmentAnalyses() {

        final List<ProductReplenishmentAnalysis> analyses = new ArrayList<>(32);

        final String SQL_SELECT = "SELECT DISTINCT a.sku_id, a.product_id, b.status, a.low_water_mark_count, " +
                "a.high_water_mark_count, b.available_count, " +
                "(a.high_water_mark_count - b.available_count) AS replenishment_count " +
                "FROM product_inventory_benchmarks AS a " +
                "INNER JOIN product_inventory_levels b ON (a.sku_id = b.sku_id) " +
                "WHERE (a.high_water_mark_count - b.available_count) > 0 ";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String skuId = resultSet.getString("sku_id");
                int productId = resultSet.getInt("product_id");
                int availableCount = resultSet.getInt("available_count");
                String status = resultSet.getString("status");
                int lwm = resultSet.getInt("low_water_mark_count");
                int hwm = resultSet.getInt("high_water_mark_count");
                int replenishmentCount = resultSet.getInt("replenishment_count");


                final ProductReplenishmentAnalysis resultItem = new ProductReplenishmentAnalysis(skuId, productId,
                        availableCount, status, lwm, hwm, replenishmentCount);

                analyses.add(resultItem);

            }

            return analyses;

        } catch (Exception e) {
            throw new RuntimeException("DB Connection error during query", e);
        }
    }
}