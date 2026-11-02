package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

@Entity
@Table(name = "TCHARGES", uniqueConstraints = @UniqueConstraint(columnNames = { "invoice_id", "paymentmean_id" }))
public class Charge extends BaseEntity {

    // Natural attributes
    @AttributeOverride(name = "amount", column = @Column(name = "charge", precision = 19, scale = 2))
    private Money amount; // cents

    // Accidental attributes
    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private PaymentMean paymentMean;

    // ------ Constructors -----

    Charge() {
    }

    /* package */ Charge(Invoice invoice, PaymentMean paymentMean, Money amount) {
        // validates the arguments
        ArgumentChecks.isNotNull(invoice, "Invoice cannot be null");
        ArgumentChecks.isNotNull(paymentMean, "Payment mean cannot be null");
        ArgumentChecks.isNotNull(amount, "Amount cannot be null");
        ArgumentChecks.isTrue(amount.isNonNegative(), "Amount must be positive");
        // store the amount rounded to cents
        this.amount = amount.roundToCents();
        // increments the paymentMean accumulated -> paymentMean.charge( amount )
        paymentMean.charge(amount);
        // links invoice, this and paymentMean
        Associations.SettlesWithdraws.atomicLink(invoice, this, paymentMean);
    }

    // ---- Getters ----

    public Money getAmount() {
        return amount;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public PaymentMean getPaymentMean() {
        return paymentMean;
    }

    // ---- Bussiness methods ----

    /**
     * Restores and the value to the payment mean and unlinks the charge from the
     * invoice and the payment mean.
     */
    /* package */ void rewind() {
        // decrements the payment mean accumulated ( paymentMean.rewind( amount) )
        StateChecks.isTrue(invoice != null && paymentMean != null, "Charge has already been rewound");
        paymentMean.rewind(amount);
        Associations.SettlesWithdraws.atomicUnlink(this);
    }

    // ---- Association helpers ----

    void _setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    void _setPaymentMean(PaymentMean paymentMean) {
        this.paymentMean = paymentMean;
    }

    // ---- toString ----
    @Override
    public String toString() {
        return "Charge [invoice=" + invoice + ", paymentMean=" + paymentMean + ", amount=" + amount + "]";
    }
}
