package com.izzyacademy.data.generators.models;

public class ProductReplenishmentAnalysis {

    private String skuId;

    private int productId;

    private int availableCount;

    private String status;

    private int highWaterMark;

    private int lowWaterMark;

    private int replenishmentCount;

    public ProductReplenishmentAnalysis() {

    }

    public ProductReplenishmentAnalysis(String skuId, int productId, int availableCount, String status,
                                        int highWaterMark, int lowWaterMark, int replenishmentCount) {
        this.skuId = skuId;
        this.productId = productId;
        this.availableCount = availableCount;
        this.status = status;
        this.highWaterMark = highWaterMark;
        this.lowWaterMark = lowWaterMark;
        this.replenishmentCount = replenishmentCount;
    }

    public int getHighWaterMark() {
        return highWaterMark;
    }

    public void setHighWaterMark(int highWaterMark) {
        this.highWaterMark = highWaterMark;
    }

    public int getLowWaterMark() {
        return lowWaterMark;
    }

    public void setLowWaterMark(int lowWaterMark) {
        this.lowWaterMark = lowWaterMark;
    }

    public int getReplenishmentCount() {
        return replenishmentCount;
    }

    public void setReplenishmentCount(int replenishmentCount) {
        this.replenishmentCount = replenishmentCount;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProductReplenishmentAnalysis{" +
                "skuId='" + skuId + '\'' +
                ", productId=" + productId +
                ", availableCount=" + availableCount +
                ", status='" + status + '\'' +
                ", highWaterMark=" + highWaterMark +
                ", lowWaterMark=" + lowWaterMark +
                ", replenishmentCount=" + replenishmentCount +
                '}';
    }
}
