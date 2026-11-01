package uo.ri.cws.domain;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class Cash extends PaymentMean {

    // ---- Constructors ----

    public Cash(Client client) {
        ArgumentChecks.isNotNull(client, "Client cannot be null");
        Associations.Holds.link(client, this);
    }

    // ---- Business methods ----
    @Override
    public boolean canBeCharged(Money amount) {
        checkPositive(amount);
        return true;
    }

    // ---- toString ----
    @Override
    public String toString() {
        return "Cash [accumulated=" + getAccumulated() + ", client=" + getClient() + "]";
    }

    @Override
    public int hashCode() {
        return getClient().hashCode();
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
        Cash other = (Cash) obj;
        return getClient().equals(other.getClient());
    }

}
