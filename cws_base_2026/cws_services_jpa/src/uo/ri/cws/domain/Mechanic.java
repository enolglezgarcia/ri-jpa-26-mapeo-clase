package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;

public class Mechanic {

    // Natural attributes
    private Nif nif;
    private FullName fullName;

    // Accidental attributes
    private Set<WorkOrder> assigned = new HashSet<>();

    public Mechanic(Nif nif) {
        // Delegate to the other constructor with meaningful default values for the
        // fullname
        this(nif, new FullName("Default", "Mechanic"));
    }

    public Mechanic(Nif nif, FullName name) {
        ArgumentChecks.isNotNull(nif, "Mechanic NIF cannot be null");
        ArgumentChecks.isNotNull(name, "Mechanic full name cannot be null");
        this.nif = nif;
        this.fullName = name;
    }

    // ---- Setters (optional, controlled) ----

    public void setFullName(FullName name) {
        ArgumentChecks.isNotNull(name);
        this.fullName = name;
    }

    public void setNif(Nif nif) {
        ArgumentChecks.isNotNull(nif);
        this.nif = nif;
    }

    // ---- Associations helpers ----

    Set<WorkOrder> _getAssigned() {
        return assigned;
    }

    // Getters

    public Nif getNif() {
        return nif;
    }

    public FullName getFullName() {
        return fullName;
    }

    public Set<WorkOrder> getAssigned() {
        return new HashSet<>(assigned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nif);
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
        Mechanic other = (Mechanic) obj;
        return Objects.equals(nif, other.nif);
    }

    @Override
    public String toString() {
        return "Mechanic [nif=" + nif + ", fullName=" + fullName + "]";
    }

}
