## DataGenerators

Data Generators for Simulating Real Time Stream Analysis Scenarios

We have the following components

- Order Generator Micro Service
- Order Shipments Micro Service
- Order Delivery Micro Service
- Order Returns Micro Service
- Inventory Replenishment Micro Service

Just as their names suggest, these services creates new orders, ship orders in fulfilment status, delivers the shipped items and also simulates customer returns.

There is also a micro service that simulates suppliers replenishing inventory items that have dropped below the low water mark.

Inventory items are not being replenished if they are above the high water mark.

The inventory count is always changing as new orders are being placed and returned by the customers to the store.


## Installation of Data Generator App

The values file in the HelmCharts directory contains config settings you can adjust to your own preferences

````shell script

cd HelmCharts

# Adjust the config settings if necessary to your preferences
vim data-generators/values.yaml

````

You can run the following commands below to install or uninstall all the data generator micro services

````shell script

helm upgrade --install data-generators ./data-generators

helm uninstall data-generators

````

If you want to run the service locally, you can compile it with Maven and then set the environment variables before running the jar files

````shell script

# Compile the application
mvn clean package

# Set the service name you would like to execute
export SERVICE_NAME=OrderGeneratorService

# Run the micro service to generate data
java -jar target/data-generator-1.0.0-uber.jar 

```
