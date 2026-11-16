import java.time.DayOfWeek;
import java.util.Set;

/**
 * A physiotherapist: helps patients recover movement and manage pain.
 */
public final class Physiotherapist extends HealthProfessional {
    private final int yearsExperience;

    /**
     * Creates a physiotherapist with no recorded experience and an all-week
     * roster.
     *
     * @param id   digits-only identifier
     * @param name display name
     */
    public Physiotherapist(String id, String name) {
        this(id, name, 0);
    }

    /**
     * Creates a physiotherapist with the default all-week roster.
     *
     * @param id              digits-only identifier
     * @param name            display name
     * @param yearsExperience completed years of practice; must not be negative
     */
    public Physiotherapist(String id, String name, int yearsExperience) {
        super(id, name);
        this.yearsExperience = checkYears(yearsExperience);
    }

    /**
     * Creates a physiotherapist with an explicit roster.
     *
     * @param id              digits-only identifier
     * @param name            display name
     * @param workingDays     the days this physiotherapist works
     * @param yearsExperience completed years of practice; must not be negative
     */
    public Physiotherapist(String id, String name, Set<DayOfWeek> workingDays,
                           int yearsExperience) {
        super(id, name, workingDays);
        this.yearsExperience = checkYears(yearsExperience);
    }

    private static int checkYears(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years of experience must not be negative.");
        }
        return years;
    }

    @Override
    public String role() {
        return "Physiotherapist";
    }

    @Override
    public String service() {
        return "Assesses movement and prescribes exercise therapy.";
    }

    @Override
    protected String extra() {
        return "experience=" + yearsExperience + " years";
    }
}
