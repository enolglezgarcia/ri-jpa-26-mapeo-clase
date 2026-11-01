package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.LicensePlate;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
public class Vehicle extends BaseEntity {

    // Natural attributes
    @AttributeOverride(name = "licensePlate", column = @Column(unique = true))
    private LicensePlate licensePlate;
    private String make;
    private String model;

    // Accidental attributes
    @ManyToOne
    private Client client;
    @ManyToOne
    private VehicleType vehicleType;
    @OneToMany(mappedBy = "vehicle")
    private Set<WorkOrder> workOrders = new HashSet<>();

    // ---- Constructors ----

    Vehicle() {

    }

    public Vehicle(LicensePlate plateNumber) {
        // Delegate to the other constructor with meaningful default values for make and
        // model
        this(plateNumber, "No make", "No Model");
    }

    public Vehicle(LicensePlate plateNumber, String make, String model) {
        ArgumentChecks.isNotNull(plateNumber);
        ArgumentChecks.isNotBlank(make);
        ArgumentChecks.isNotBlank(model);

        this.licensePlate = plateNumber;
        this.make = make;
        this.model = model;
    }

    public Money getPricePerHour() {
        return vehicleType.getPricePerHour(); // VehicleType must return Money
    }

    public LicensePlate getLicensePlate() {
        return licensePlate;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Client getClient() {
        return client;
    }

    public Set<WorkOrder> getWorkOrders() {
        return new HashSet<>(workOrders);
    }

    Set<WorkOrder> _getWorkOrders() {
        return workOrders;
    }

    // association helpers
    /* package */
    void _setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    /* package */
    void _setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Vehicle [licensePlate=" + licensePlate + ", make=" + make + ", model=" + model + "]";
    }

}
