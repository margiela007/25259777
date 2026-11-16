import java.time.DayOfWeek;
import java.util.Set;

/**
 * A general practitioner: the first point of contact for most patients.
 */
public final class GeneralPractitioner extends HealthProfessional {
    private final boolean acceptingNewPatients;

    /**
     * Creates a GP who accepts new patients and works every day.
     *
     * @param id   digits-only identifier
     * @param name display name
     */
    public GeneralPractitioner(String id, String name) {
        this(id, name, true);
    }

    /**
     * Creates a GP with the default all-week roster.
     *
     * @param id                   digits-only identifier
     * @param name                 display name
     * @param acceptingNewPatients whether the GP takes new patients
     */
    public GeneralPractitioner(String id, String name, boolean acceptingNewPatients) {
        super(id, name);
        this.acceptingNewPatients = acceptingNewPatients;
    }

    /**
     * Creates a GP with an explicit roster.
     *
     * @param id                   digits-only identifier
     * @param name                 display name
     * @param workingDays          the days this GP works
     * @param acceptingNewPatients whether the GP takes new patients
     */
    public GeneralPractitioner(String id, String name, Set<DayOfWeek> workingDays,
                               boolean acceptingNewPatients) {
        super(id, name, workingDays);
        this.acceptingNewPatients = acceptingNewPatients;
    }

    @Override
    public String role() {
        return "General practitioner";
    }

    @Override
    public String service() {
        return "Consults, diagnoses and prescribes medication.";
    }

    @Override
    protected String extra() {
        return "accepting new patients=" + acceptingNewPatients;
    }
}
