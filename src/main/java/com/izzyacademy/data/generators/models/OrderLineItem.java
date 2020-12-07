package com.izzyacademy.data.generators.models;

public class OrderLineItem {

    private long lineItemId;

    private int itemCount;

    private String skuId;

    private int productId;

    private int orderId;

    private int customerId;

    private String status;

    public OrderLineItem() {}

    public OrderLineItem(long lineItemId, int itemCount, String skuId, int productId,
                         int orderId, int customerId, String status) {
        this.lineItemId = lineItemId;
        this.itemCount = itemCount;
        this.skuId = skuId;
        this.productId = productId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
    }

    public long getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(long lineItemId) {
        this.lineItemId = lineItemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderLineItem{" +
                "lineItemId=" + lineItemId +
                ", itemCount=" + itemCount +
                ", skuId='" + skuId + '\'' +
                ", productId=" + productId +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", status='" + status + '\'' +
                '}';
    }
}
