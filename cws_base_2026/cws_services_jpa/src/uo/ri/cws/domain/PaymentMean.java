package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

@Entity
public abstract class PaymentMean extends BaseEntity {

    // Natural attributes
    @AttributeOverride(name = "amount", column = @Column(name = "accumulated", precision = 19, scale = 2))
    private Money accumulated = Money.ZERO; // cents

    // Accidental attributes
    @ManyToOne
    private Client client;
    @OneToMany(mappedBy = "paymentMean")
    private Set<Charge> charges = new HashSet<>();

    // ---- Association helpers ----

    void _setClient(Client client) {
        this.client = client;
    }

    Set<Charge> _getCharges() {
        return charges;
    }

    // ---- Getters ----
    public Client getClient() {
        return client;
    }

    public Money getAccumulated() {
        return accumulated;
    }

    public Set<Charge> getCharges() {
        return new HashSet<>(charges);
    }

    // ---- Business methods ----

    public abstract boolean canBeCharged(Money amount);

    /**
     * Increments the accumulated amount of the payment mean by the given amount.
     * package-private Only called from Charge constructor.
     * 
     * @param amount
     */
    /* package */ void charge(Money amount) {
        // Normalize the amount to cents
        amount = normalizePositive(amount);
        // check if it can be charged
        StateChecks.isTrue(canBeCharged(amount), "Payment mean cannot be charged with the given amount");
        // increment the accumulated amount
        accumulated = Money.calc(accumulated).add(amount).toMoneyRoundedToCents();

    }

    /**
     * Decrements the accumulated amount of the payment mean by the given amount.
     * package-private Only called from Charge.rewind().
     * 
     * @param amount
     */
    /* package */ void rewind(Money amount) {
        checkPositive(amount);
        StateChecks.isTrue(accumulated.greaterOrEqual(amount), "Cannot rewind more than the accumulated amount");
        accumulated = Money.calc(accumulated).subtract(amount).toMoneyExact();
    }

    protected static void checkPositive(Money amount) {
        ArgumentChecks.isNotNull(amount, "Amount cannot be null");
        ArgumentChecks.isTrue(amount.greaterThan(Money.ZERO), "Amount must be positive");
    }

    protected static Money normalizePositive(Money amount) {
        ArgumentChecks.isNotNull(amount, "Amount cannot be null");
        Money result = Money.calc(amount).toMoneyRoundedToCents();
        checkPositive(result);
        return result;
    }

}
