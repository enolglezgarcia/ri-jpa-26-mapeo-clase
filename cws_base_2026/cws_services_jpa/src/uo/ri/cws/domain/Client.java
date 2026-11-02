package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Address;
import uo.ri.cws.domain.types.Email;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.domain.types.PhoneNumber;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TCLIENTS")
public class Client extends BaseEntity {

    // Natural attributes
    @AttributeOverride(name = "nif", column = @Column(unique = true))
    private Nif nif;
    private FullName fullName;
    private Email email;
    private PhoneNumber phone;
    private Address address;

    // Accidental attributes
    @OneToMany(mappedBy = "client")
    private Set<Vehicle> vehicles = new HashSet<>();
    @OneToMany(mappedBy = "client")
    private Set<PaymentMean> paymentMeans = new HashSet<>();

    // ---- Constructors ----
    Client() {

    }

    public Client(Nif nif) {
        this(nif, new FullName("No name", "No surname"), new Email("default@example.com"),
            new PhoneNumber("+34600000000"), new Address("No street", "No city", "No zip", "No country"));
    }

    public Client(Nif nif, FullName fullName) {
        this(nif, fullName, new Email("default@example.com"), new PhoneNumber("+34600000000"),
            new Address("No street", "No city", "No zip", "No country"));
    }

    public Client(Nif nif, FullName fullName, Email email, PhoneNumber phone, Address address) {
        ArgumentChecks.isNotNull(nif, "Client NIF cannot be null");
        ArgumentChecks.isNotNull(fullName, "Client full name cannot be null");
        ArgumentChecks.isNotNull(email, "Client email cannot be null");
        ArgumentChecks.isNotNull(phone, "Client phone number cannot be null");
        ArgumentChecks.isNotNull(address, "Client address cannot be null");
        this.nif = nif;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Nif getNif() {
        return nif;
    }

    public FullName getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    public Set<Vehicle> getVehicles() {
        return new HashSet<>(vehicles);
    }

    public Set<PaymentMean> getPaymentMeans() {
        return new HashSet<>(paymentMeans);
    }

    // Association helpers
    /* package */
    Set<Vehicle> _getVehicles() {
        return vehicles;
    }

    /* package */
    Set<PaymentMean> _getPaymentMeans() {
        return paymentMeans;
    }

}