import java.time.DayOfWeek;
import java.util.Set;

/**
 * A cardiologist: a specialist who focuses on the heart and circulation.
 */
public final class Cardiologist extends HealthProfessional {
    private final String subspecialty;

    /**
     * Creates a cardiologist with a default subspecialty and all-week roster.
     *
     * @param id   digits-only identifier
     * @param name display name
     */
    public Cardiologist(String id, String name) {
        this(id, name, "general cardiology");
    }

    /**
     * Creates a cardiologist with the default all-week roster.
     *
     * @param id           digits-only identifier
     * @param name         display name
     * @param subspecialty the area of cardiology this doctor focuses on
     */
    public Cardiologist(String id, String name, String subspecialty) {
        super(id, name);
        this.subspecialty = Check.text(subspecialty, "Subspecialty");
    }

    /**
     * Creates a cardiologist with an explicit roster.
     *
     * @param id           digits-only identifier
     * @param name         display name
     * @param workingDays  the days this cardiologist works
     * @param subspecialty the area of cardiology this doctor focuses on
     */
    public Cardiologist(String id, String name, Set<DayOfWeek> workingDays,
                        String subspecialty) {
        super(id, name, workingDays);
        this.subspecialty = Check.text(subspecialty, "Subspecialty");
    }

    @Override
    public String role() {
        return "Cardiologist";
    }

    @Override
    public String service() {
        return "Assesses heart conditions and plans treatment.";
    }

    @Override
    protected String extra() {
        return "subspecialty=" + subspecialty;
    }
}
