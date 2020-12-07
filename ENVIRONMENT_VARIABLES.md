

### Setting Environment Variables

#### Running the OrderGeneratorService Micro Service
To run OrderGeneratorService, set this environment variable

````shell script
export SERVICE_NAME="OrderGeneratorService"
````

#### Running the InventoryReplenishmentService Micro Service
To run InventoryReplenishmentService, set this environment variable

````shell script
export SERVICE_NAME="InventoryReplenishmentService"
````

#### Running the OrderReturnsService Micro Service
To run OrderReturnsService, set this environment variable

````shell script
export SERVICE_NAME="OrderReturnsService"
````

#### Running the OrderShipmentService Micro Service
To run OrderShipmentService, set this environment variable

````shell script
export SERVICE_NAME="OrderShipmentService"
````

#### Running the OrderDeliveryService Micro Service
To run OrderDeliveryService, set this environment variable

````shell script
export SERVICE_NAME="OrderDeliveryService"
````

#### Updating Application Constants

````shell script

    # How many seconds to wait before placing orders
    export MIN_ORDER_INTERVAL_SECONDS=1
    export MAX_ORDER_INTERVAL_SECOND=15

    # How many seconds orders are shipped
    export SHIPMENT_INTERVAL_SECONDS=5
    export SHIPMENT_COUNT=10

    # How many seconds orders are returned
    export ORDER_RETURN_INTERVAL_SECONDS=5
    export ORDER_RETURN_AGE_SECONDS=5
    export ORDER_RETURN_COUNT=10

````