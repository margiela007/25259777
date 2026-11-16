import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Common state and behaviour for every kind of health professional.
 *
 * <p>The class is abstract because a "plain" health professional with no
 * specialty does not exist in this domain. Every concrete subtype must say
 * what it is ({@link #role()}), what it does ({@link #service()}) and what
 * extra detail it holds ({@link #extra()}).</p>
 */
public abstract class HealthProfessional {
    private final String id;
    private final String name;
    private final EnumSet<DayOfWeek> workingDays;

    /**
     * Convenience constructor: works every day of the week.
     *
     * <p>This default keeps the demonstration runnable on any date. Real
     * rosters should use the three-argument constructor below.</p>
     *
     * @param id   digits-only identifier
     * @param name display name
     */
    protected HealthProfessional(String id, String name) {
        this(id, name, EnumSet.allOf(DayOfWeek.class));
    }

    /**
     * Creates a professional with an explicit weekly roster.
     *
     * @param id          digits-only identifier
     * @param name        display name
     * @param workingDays the days this professional works; copied defensively
     * @throws IllegalArgumentException when the id, name or roster is invalid
     */
    protected HealthProfessional(String id, String name, Set<DayOfWeek> workingDays) {
        this.id = Check.id(id);
        this.name = Check.text(name, "Name");
        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalArgumentException("At least one working day is required.");
        }
        EnumSet<DayOfWeek> copy = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek day : workingDays) {
            if (day == null) {
                throw new IllegalArgumentException("Working days must not contain null.");
            }
            copy.add(day);
        }
        this.workingDays = copy;
    }

    /** @return the digits-only identifier */
    public final String getId() {
        return id;
    }

    /** @return the display name */
    public final String getName() {
        return name;
    }

    /**
     * Returns a read-only copy of the roster, so callers cannot change the
     * professional's availability after construction.
     *
     * @return an unmodifiable copy of the working days
     */
    public final Set<DayOfWeek> getWorkingDays() {
        return Collections.unmodifiableSet(EnumSet.copyOf(workingDays));
    }

    /**
     * @param day the day to test
     * @return true when this professional works on that day
     */
    public final boolean worksOn(DayOfWeek day) {
        return workingDays.contains(day);
    }

    /**
     * Short one-line description for places where the full roster would be
     * noise, such as an appointment listing.
     *
     * @return the role, identifier and name
     */
    public final String summary() {
        return role() + " #" + id + " " + name;
    }

    /** @return the human-readable kind of professional */
    public abstract String role();

    /** @return a one-line description of the service this professional provides */
    public abstract String service();

    /** @return subtype-specific details, appended to {@link #toString()} */
    protected abstract String extra();

    /**
     * Template method: the shared format is defined once, while each subtype
     * supplies its own role, service and extra detail through dynamic dispatch.
     */
    @Override
    public String toString() {
        return role() + " #" + id + " | " + name + " | days=" + workingDays + " | " + extra();
    }

    /**
     * Two professionals are the same person when their identifiers match,
     * regardless of which subtype the objects have.
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional professional = (HealthProfessional) other;
        return id.equals(professional.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
