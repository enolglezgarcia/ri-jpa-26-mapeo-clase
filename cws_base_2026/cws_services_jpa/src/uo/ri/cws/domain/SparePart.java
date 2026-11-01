package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
public class SparePart extends BaseEntity {

    // Natural attributes
    @Column(unique = true)
    private String code;
    private String description;
    @AttributeOverride(name = "amount", column = @Column(name = "unitPrice", precision = 19, scale = 2))
    private Money unitPrice;

    SparePart() {
    }

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
    public String toString() {
        return "SparePart [code=" + code + ", description=" + description + ", unitPrice=" + unitPrice + "]";
    }

}
