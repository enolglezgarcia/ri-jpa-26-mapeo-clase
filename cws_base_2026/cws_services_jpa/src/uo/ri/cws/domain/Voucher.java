package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.cws.domain.types.VoucherCode;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

public class Voucher extends PaymentMean {

    private VoucherCode code;
    private String description;
    private Money availableAmount; // cents

    // ---- Constructors ----
    public Voucher(VoucherCode code, Money available) {
        this(code, "no-description", available);
    }

    public Voucher(VoucherCode code, String description, Money available) {
        ArgumentChecks.isNotNull(code);
        ArgumentChecks.isNotBlank(description);
        ArgumentChecks.isNotNull(available);
        ArgumentChecks.isTrue(available.isNonNegative(), "Available amount must be non-negative");

        this.code = code;
        this.description = description;
        this.availableAmount = Money.calc(available).toMoneyRoundedToCents();
    }

    // ------ Getters and setters (if needed) ----

    public Money getAvailableAmount() {
        return availableAmount;
    }

    public String getDescription() {
        return description;
    }

    public void changeDescription(String description) {
        ArgumentChecks.isNotBlank(description);
        this.description = description;
    }

    public VoucherCode getCode() {
        return code;
    }

    // ---- Business methods ----

    @Override
    /* package */ void charge(Money amount) {
        // Charges the amount to the payment mean and subtracts it from the available
        // amount
        StateChecks.isTrue(canBeCharged(amount), "Payment mean cannot be charged with the given amount");
        super.charge(amount);
        availableAmount = Money.calc(availableAmount).subtract(amount).toMoneyExact();
    }

    @Override
    /* package */ void rewind(Money amount) {
        // Rewinds the amount to the payment mean and adds it to the available amount
        super.rewind(amount);
        availableAmount = Money.calc(availableAmount).add(amount).toMoneyExact();
    }

    @Override
    public boolean canBeCharged(Money amount) {
        ArgumentChecks.isNotNull(amount, "amount cannot be null");
        ArgumentChecks.isTrue(amount.isNonNegative(), "amount must be non-negative");
        ArgumentChecks.isTrue(amount.greaterThan(Money.ZERO), "amount must be positive");
        return availableAmount.greaterOrEqual(amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Voucher other = (Voucher) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public String toString() {
        return "Voucher [getAccumulated()=" + getAccumulated() + ", code=" + code + ", description=" + description
            + ", availableAmount=" + availableAmount + "]";
    }

}
