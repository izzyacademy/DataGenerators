package com.izzyacademy.data.generators.models;

public class ProductInventoryLevel {

    private String skuId;

    private int productId;

    private int availableCount;

    private String status;

    public ProductInventoryLevel() {

    }

    public ProductInventoryLevel(String skuId, int productId, int availableCount, String status) {
        this.skuId = skuId;
        this.productId = productId;
        this.availableCount = availableCount;
        this.status = status;
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
        return "ProductInventoryLevel{" +
                "skuId='" + skuId + '\'' +
                ", productId=" + productId +
                ", availableCount=" + availableCount +
                ", status='" + status + '\'' +
                '}';
    }
}
