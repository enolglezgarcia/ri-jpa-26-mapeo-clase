package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * Represents an intervention performed by a mechanic on a work order. Internal
 * aggregate class of WorkOrder. Only WorkOrder can create Interventions.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "mechanic_id", "workorder_id", "date" }))
public class Intervention extends BaseEntity {
    // Natural attributes
    private LocalDateTime date;
    private int minutes;

    // Accidental attributes
    @ManyToOne
    private WorkOrder workOrder;
    @ManyToOne
    private Mechanic mechanic;
    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Substitution> substitutions = new HashSet<>();

    // ---------------------------- Constructors -----------------------------
    Intervention() {
    }

    /**
     * All these constructors are package-private Only WorkOrder (aggregate root)
     * can create Interventions
     */
    /* package */ Intervention(Mechanic mechanic, WorkOrder workOrder, int minutes) {
        this(mechanic, workOrder, LocalDateTime.now(), minutes);
    }

    /* package */ Intervention(Mechanic mechanic, WorkOrder workOrder, LocalDateTime date, int minutes) {
        this(mechanic, workOrder, date, minutes, Map.of());
    }

    /**
     * Creates a new Intervention with the given parameters.
     * 
     * @param mechanic      The mechanic performing the intervention.
     * @param workOrder     The work order associated with the intervention.
     * @param date          The date and time of the intervention.
     * @param minutes       The duration of the intervention in minutes.
     * @param substitutions A map of spare parts and their quantities used in the
     *                      intervention.
     */
    /* package */ Intervention(Mechanic mechanic, WorkOrder workOrder, LocalDateTime date, int minutes,
        Map<SparePart, Integer> substitutions) {

        // Validate input parameters
        ArgumentChecks.isNotNull(mechanic, "Mechanic cannot be null");
        ArgumentChecks.isNotNull(workOrder, "Work order cannot be null");
        ArgumentChecks.isNotNull(date, "Date cannot be null");
        ArgumentChecks.isTrue(minutes >= 0, "Minutes must be non-negative");
        ArgumentChecks.isNotNull(substitutions, "Substitutions map cannot be null");
        validateSubstitutions(substitutions);
        // Truncate the date to milliseconds to avoid precision issues
        this.date = date.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
        // links the intervention with the work order and mechanic (atomiclink)
        Associations.RecordsIntervenes.atomicLink(workOrder, this, mechanic);
        // For each substitution in the map, add it to the intervention
        for (Map.Entry<SparePart, Integer> entry : substitutions.entrySet()) {
            SparePart sparePart = entry.getKey();
            Integer quantity = entry.getValue();
            addSubstitution(sparePart, quantity);
        }
        this.minutes = minutes;
    }

    // ---------------------------- Getters -----------------------------

    // ---------------------------- Association helpers
    // -----------------------------
    void _setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getMinutes() {
        return minutes;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    void _setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public Set<Substitution> getSubstitutions() {
        return new HashSet<>(substitutions);
    }

    Set<Substitution> _getSubstitutions() {
        return substitutions;
    }

    // ---------------------------- Business methods -----------------------------

    /* package */ void removeSubstitutions() {
        for (Substitution substitution : new HashSet<>(substitutions)) {
            Associations.PerformsSubstitutes.atomicUnlink(substitution);
        }
    }

    /**
     * Calculates the total amount of the intervention, including work and spare
     * parts.
     * 
     * @return The total amount of the intervention rounded to cents as a Money
     *         object.
     */
    public Money getTotalAmount() {
        Money workAmount = calculateWorkAmount();
        Money sparesAmount = calculateSparesAmount();
        Money totalAmount = Money.ZERO;
        totalAmount = Money.calc(totalAmount).add(workAmount).add(sparesAmount).toMoneyRoundedToCents();
        return totalAmount;
    }

    private Money calculateWorkAmount() {
        Money pricePerHour = workOrder.getPricePerHour();

        return Money.calc(pricePerHour).times(minutes).div(60) // mins/hour
            .toMoneyExact();
    }

    private Money calculateSparesAmount() {
        Money totalPrice = Money.ZERO;
        for (Substitution s : substitutions) {
            Money price = s.getSparePart().getPrice();
            int quantity = s.getQuantity();
            Money substitutionPrice = Money.ZERO;
            substitutionPrice = Money.calc(price).times(quantity).toMoneyExact();
            totalPrice = Money.calc(totalPrice).add(substitutionPrice).toMoneyExact();
        }

        return totalPrice;
    }

    private void addSubstitution(SparePart sparePart, int quantity) {
        new Substitution(sparePart, this, quantity);
    }

    private static void validateSubstitutions(Map<SparePart, Integer> substitutions) {
        for (Map.Entry<SparePart, Integer> entry : substitutions.entrySet()) {
            SparePart sparePart = entry.getKey();
            Integer quantity = entry.getValue();
            ArgumentChecks.isNotNull(sparePart, "Spare part cannot be null");
            ArgumentChecks.isTrue(quantity > 0, "Quantity must be positive");
        }
    }

    @Override
    public String toString() {
        return "Intervention [date=" + date + ", minutes=" + minutes + ", workOrder=" + workOrder + ", mechanic="
            + mechanic + "]";
    }

}
