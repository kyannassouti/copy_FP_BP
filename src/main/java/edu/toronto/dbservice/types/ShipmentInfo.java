package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ShipmentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;
    private Integer farmerAccount;
    private Integer carrierAccount;
    private String status;
    private Integer warehouseId;
    private String rackLocation;

    public ShipmentInfo(Integer shipmentId, Integer farmerAccount, Integer carrierAccount, String status, Integer warehouseId, String rackLocation) {
        this.shipmentId = shipmentId;
        this.farmerAccount = farmerAccount;
        this.carrierAccount = carrierAccount;
        this.status = status;
        this.warehouseId = warehouseId;
        this.rackLocation = rackLocation;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public Integer getFarmerAccount() {
        return farmerAccount;
    }

    public Integer getCarrierAccount() {
        return carrierAccount;
    }

    public String getStatus() {
        return status;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public String getRackLocation() {
        return rackLocation;
    }
}
