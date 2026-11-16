import java.time.DayOfWeek;
import java.util.Set;

/**
 * A nurse: supports consultations and carries out clinical tasks such as
 * taking blood samples.
 */
public final class Nurse extends HealthProfessional {
    private final String registration;

    /**
     * Creates a nurse with a default registration and an all-week roster.
     *
     * @param id   digits-only identifier
     * @param name display name
     */
    public Nurse(String id, String name) {
        this(id, name, "Registered nurse");
    }

    /**
     * Creates a nurse with the default all-week roster.
     *
     * @param id           digits-only identifier
     * @param name         display name
     * @param registration the nurse's registration level
     */
    public Nurse(String id, String name, String registration) {
        super(id, name);
        this.registration = Check.text(registration, "Registration");
    }

    /**
     * Creates a nurse with an explicit roster.
     *
     * @param id           digits-only identifier
     * @param name         display name
     * @param workingDays  the days this nurse works
     * @param registration the nurse's registration level
     */
    public Nurse(String id, String name, Set<DayOfWeek> workingDays,
                 String registration) {
        super(id, name, workingDays);
        this.registration = Check.text(registration, "Registration");
    }

    @Override
    public String role() {
        return "Nurse";
    }

    @Override
    public String service() {
        return "Takes blood samples and provides nursing care.";
    }

    @Override
    protected String extra() {
        return "registration=" + registration;
    }
}
