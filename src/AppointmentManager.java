import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Owns one day's appointments and enforces the clinic's booking rules:
 * the time must be on the schedule, the professional must work that day, and
 * the same professional must not be booked twice at the same time.
 *
 * <p>Methods return a short message instead of printing, so the class can be
 * reused by a console demonstration, a test, or later by a user interface.</p>
 */
public final class AppointmentManager {
    private final LocalDate day;
    private final ClinicSchedule schedule;
    private final List<Appointment> appointments = new ArrayList<>();

    /** Creates a manager for today with the standard schedule. */
    public AppointmentManager() {
        this(LocalDate.now());
    }

    /**
     * @param day the day this manager is responsible for
     */
    public AppointmentManager(LocalDate day) {
        this(day, ClinicSchedule.standard());
    }

    /**
     * @param day      the day this manager is responsible for
     * @param schedule the clinic's available start times
     * @throws IllegalArgumentException when either argument is null
     */
    public AppointmentManager(LocalDate day, ClinicSchedule schedule) {
        if (day == null) {
            throw new IllegalArgumentException("A day is required.");
        }
        if (schedule == null) {
            throw new IllegalArgumentException("A schedule is required.");
        }
        this.day = day;
        this.schedule = schedule;
    }

    /** @return the day this manager covers */
    public LocalDate getDay() {
        return day;
    }

    /** @return the schedule this manager uses */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Builds and adds an appointment from raw text input, turning any
     * validation failure into a warning message.
     *
     * @param patientName  the patient's name
     * @param phone        the patient's phone number
     * @param timeText     the start time as HH:mm
     * @param professional the chosen health professional
     * @return an "OK:" message or a "WARNING:" message
     */
    public String book(String patientName, String phone, String timeText,
                       HealthProfessional professional) {
        try {
            LocalTime time = parseTime(timeText);
            return add(new Appointment(new Patient(patientName, phone), professional, time));
        } catch (IllegalArgumentException error) {
            return "WARNING: " + error.getMessage();
        }
    }

    /**
     * Adds an already-built appointment after checking the clinic rules.
     *
     * @param appointment the appointment to add
     * @return an "OK:" message or a "WARNING:" message
     */
    public String add(Appointment appointment) {
        if (appointment == null) {
            return "WARNING: An appointment is required.";
        }
        if (!schedule.has(appointment.getTime())) {
            return "WARNING: " + appointment.getTime() + " is not a clinic slot.";
        }
        if (!appointment.getProfessional().worksOn(day.getDayOfWeek())) {
            return "WARNING: " + appointment.getProfessional().getName()
                    + " does not work on " + day.getDayOfWeek() + ".";
        }
        for (Appointment existing : appointments) {
            boolean sameProfessional =
                    existing.getProfessional().equals(appointment.getProfessional());
            if (sameProfessional && existing.getTime().equals(appointment.getTime())) {
                return "WARNING: " + appointment.getProfessional().getName()
                        + " is already booked at " + appointment.getTime() + ".";
            }
        }
        appointments.add(appointment);
        return "OK: " + appointment.getPatient().getName() + " booked with "
                + appointment.getProfessional().getName() + " at " + appointment.getTime() + ".";
    }

    /**
     * @return a copy of every appointment, in the order they were booked
     */
    public List<Appointment> getAll() {
        return new ArrayList<>(appointments);
    }

    /**
     * @param professionalId the digits-only professional id
     * @return appointments for that id; an empty list when nothing matches
     */
    public List<Appointment> byProfessional(String professionalId) {
        ArrayList<Appointment> matches = new ArrayList<>();
        if (professionalId == null) {
            return matches;
        }
        for (Appointment appointment : appointments) {
            if (appointment.getProfessional().getId().equals(professionalId)) {
                matches.add(appointment);
            }
        }
        return matches;
    }

    /**
     * @param phone the patient's phone number
     * @return appointments for that phone; an empty list when nothing matches
     */
    public List<Appointment> byPhone(String phone) {
        ArrayList<Appointment> matches = new ArrayList<>();
        if (phone == null) {
            return matches;
        }
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getPhone().equals(phone)) {
                matches.add(appointment);
            }
        }
        return matches;
    }

    /**
     * Sorts any list of appointments without changing the caller's list.
     *
     * @param source the list to sort; may be the full book or a filtered subset
     * @return a new list in time order
     * @throws IllegalArgumentException when the list or one of its elements is null
     */
    public List<Appointment> sorted(List<Appointment> source) {
        if (source == null) {
            throw new IllegalArgumentException("A list of appointments is required.");
        }
        ArrayList<Appointment> copy = new ArrayList<>(source);
        for (Appointment appointment : copy) {
            if (appointment == null) {
                throw new IllegalArgumentException("The list must not contain null.");
            }
        }
        Collections.sort(copy);
        return copy;
    }

    /**
     * Cancels the appointment for one professional at one time.
     *
     * @param professionalId the digits-only professional id
     * @param timeText       the start time as HH:mm
     * @return an "OK:" message or a "WARNING:" message
     */
    public String cancel(String professionalId, String timeText) {
        final String id;
        final LocalTime time;
        try {
            id = Check.id(professionalId);
            time = parseTime(timeText);
        } catch (IllegalArgumentException error) {
            return "WARNING: " + error.getMessage();
        }
        Iterator<Appointment> iterator = appointments.iterator();
        while (iterator.hasNext()) {
            Appointment appointment = iterator.next();
            boolean sameProfessional = appointment.getProfessional().getId().equals(id);
            if (sameProfessional && appointment.getTime().equals(time)) {
                iterator.remove();
                return "OK: Cancelled " + appointment.getProfessional().getName()
                        + " at " + time + ".";
            }
        }
        return "WARNING: No appointment for professional " + id + " at " + time + ".";
    }

    /**
     * Cancels every appointment that matches the phone number.
     *
     * <p>The brief does not say whether one or all matches should go; this
     * design removes all of them and reports how many were removed.</p>
     *
     * @param phone the patient's phone number
     * @return an "OK:" message or a "WARNING:" message
     */
    public String cancelByPhone(String phone) {
        final String target;
        try {
            target = Check.phone(phone);
        } catch (IllegalArgumentException error) {
            return "WARNING: " + error.getMessage();
        }
        int removed = 0;
        Iterator<Appointment> iterator = appointments.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPatient().getPhone().equals(target)) {
                iterator.remove();
                removed++;
            }
        }
        if (removed == 0) {
            return "WARNING: No appointment for " + target + ".";
        }
        return "OK: Cancelled " + removed + " appointment(s) for " + target + ".";
    }

    /**
     * Parses HH:mm text and rejects anything else.
     *
     * @param text the time text
     * @return the parsed time
     * @throws IllegalArgumentException when the text is null, malformed or
     *                                  not a real clock time
     */
    private static LocalTime parseTime(String text) {
        if (text == null || !text.matches("[0-9]{2}:[0-9]{2}")) {
            throw new IllegalArgumentException("Time must use HH:mm, for example 09:00.");
        }
        try {
            return LocalTime.parse(text);
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Invalid time: " + text);
        }
    }
}
