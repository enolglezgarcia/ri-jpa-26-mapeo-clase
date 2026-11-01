package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class SparePart {

    // Natural attributes
    private String code;
    private String description;
    private Money unitPrice;

    public SparePart(String code) {
        this(code, "no-description", Money.ZERO);
    }

    public SparePart(String code, String description, Money price) {
        // Argument checks
        ArgumentChecks.isNotBlank(code, "Spare part code cannot be null or blank");
        ArgumentChecks.isNotBlank(description, "Spare part description cannot be null or blank");
        ArgumentChecks.isNotNull(price, "Spare part price cannot be null");
        ArgumentChecks.isTrue(price.isNonNegative(), "Spare part price must be non-negative");
        // Round the price to cents
        this.code = code;
        this.description = description;
        this.unitPrice = price.roundToCents();
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return unitPrice;
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
        SparePart other = (SparePart) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public String toString() {
        return "SparePart [code=" + code + ", description=" + description + ", unitPrice=" + unitPrice + "]";
    }

}
