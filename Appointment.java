import java.time.LocalTime;

/**
 * One booking: a patient, a health professional and a start time.
 *
 * <p>An appointment deliberately does not store a date. The clinic only books
 * for the current day, so the date belongs to the manager that owns the
 * collection, not to each individual appointment.</p>
 */
public final class Appointment implements Comparable<Appointment> {
    private final Patient patient;
    private final HealthProfessional professional;
    private final LocalTime time;

    /**
     * Creates a complete appointment. All three links are required, which is
     * how the class prevents an "incomplete" appointment from existing.
     *
     * @param patient      the patient being seen
     * @param professional any health professional subtype
     * @param time         the start time on the clinic's slot grid
     * @throws IllegalArgumentException when an argument is null
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        if (patient == null) {
            throw new IllegalArgumentException("A patient is required.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("A health professional is required.");
        }
        if (time == null) {
            throw new IllegalArgumentException("A start time is required.");
        }
        this.patient = patient;
        this.professional = professional;
        this.time = time;
    }

    /** @return the patient being seen */
    public Patient getPatient() {
        return patient;
    }

    /** @return the professional, viewed through the common base type */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /** @return the start time */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Orders appointments from earliest to latest. Appointments at the same
     * time are ordered by professional id so that output is stable from run to
     * run; the brief allows any order for those.
     */
    @Override
    public int compareTo(Appointment other) {
        int byTime = time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        return professional.getId().compareTo(other.professional.getId());
    }

    @Override
    public String toString() {
        return time + " | " + patient + " | " + professional.summary();
    }
}
