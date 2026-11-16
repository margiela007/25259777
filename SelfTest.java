import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Dependency-free checks for the reference model.
 *
 * <p>Run with:
 * {@code javac -encoding UTF-8 -d bin src/*.java tests/SelfTest.java}
 * then {@code java -cp bin SelfTest}.</p>
 */
public class SelfTest {
    private static int checks;

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError("FAILED: " + label);
        }
        checks++;
    }

    private static void mustReject(Runnable action, String label) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("FAILED (no rejection): " + label);
    }

    public static void main(String[] args) {
        HealthProfessional gp = new GeneralPractitioner("001", "Dr Green", true);
        HealthProfessional sameId = new GeneralPractitioner("001", "Another Name", false);
        HealthProfessional nurse = new Nurse("002", "Alex");
        HealthProfessional physio = new Physiotherapist("003", "Sam", 5);
        HealthProfessional cardio = new Cardiologist("004", "Dr Lee", "heart rhythm");

        check(gp.equals(sameId), "identity follows the id");
        check(gp.hashCode() == sameId.hashCode(), "equal ids share a hash");
        check(!gp.equals(nurse), "different ids are different people");
        check(!gp.equals(null), "equals(null) is false");

        EnumSet<DayOfWeek> monWedFri =
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        HealthProfessional partTime = new Nurse("005", "Jo", monWedFri, "Registered nurse");
        monWedFri.clear();
        check(partTime.worksOn(DayOfWeek.MONDAY), "constructor copies the roster");
        boolean readOnly = false;
        try {
            partTime.getWorkingDays().clear();
        } catch (UnsupportedOperationException expected) {
            readOnly = true;
        }
        check(readOnly, "roster getter is read-only");

        for (HealthProfessional professional :
                new HealthProfessional[] {gp, nurse, physio, cardio}) {
            check(professional.toString().contains(professional.role()),
                    "toString shows the role");
            check(!professional.service().isEmpty(), "service text exists");
        }

        mustReject(() -> new Nurse("A1", "Jo"), "id must be digits");
        mustReject(() -> new Nurse("006", "   "), "name must not be blank");
        mustReject(() -> new Patient("Ada", "abc"), "phone must be digits");
        mustReject(() -> new Patient(null, "0412345678"), "patient name is required");
        mustReject(() -> new Physiotherapist("007", "Sam", -1),
                "experience cannot be negative");
        mustReject(() -> new ClinicSchedule(new ArrayList<>()),
                "schedule cannot be empty");
        mustReject(() -> new ClinicSchedule(null), "schedule cannot be null");
        mustReject(() -> new Appointment(null, gp, LocalTime.of(9, 0)),
                "patient is required");
        mustReject(() -> new Appointment(new Patient("Ada", "0412345678"), null,
                LocalTime.of(9, 0)), "professional is required");
        mustReject(() -> new Appointment(new Patient("Ada", "0412345678"), gp, null),
                "time is required");

        ClinicSchedule standard = ClinicSchedule.standard();
        check(standard.has(LocalTime.of(8, 0)), "first slot");
        check(standard.has(LocalTime.of(16, 0)), "last slot");
        check(!standard.has(LocalTime.of(16, 30)), "after the last slot");

        LocalDate monday = LocalDate.of(2026, 10, 26);
        AppointmentManager manager = new AppointmentManager(monday);
        check(manager.getAll().isEmpty(), "book starts empty");
        check(manager.book("Ada", "0412345678", "10:00", gp).startsWith("OK"),
                "book a GP");
        check(manager.book("Ben", "0412345679", "10:00", nurse).startsWith("OK"),
                "same time, different professional");
        check(manager.book("Cara", "0412345680", "10:00", sameId).startsWith("WARNING"),
                "same id is a double booking");
        check(manager.book("Cara", "0412345680", "9:00", gp).startsWith("WARNING"),
                "time must be HH:mm");
        check(manager.book("Cara", "0412345680", "09:15", gp).startsWith("WARNING"),
                "time must be on the grid");
        check(manager.book("Cara", "0412345680", "16:30", gp).startsWith("WARNING"),
                "time must be inside the schedule");
        check(manager.book("Cara", "0412345680", "24:00", gp).startsWith("WARNING"),
                "invalid clock time");
        check(manager.book("", "0412345680", "11:00", gp).startsWith("WARNING"),
                "patient name is checked");
        check(manager.book("Cara", "abc", "11:00", gp).startsWith("WARNING"),
                "phone is checked");
        check(manager.book("Cara", "0412345680", "11:00", null).startsWith("WARNING"),
                "professional is checked");
        check(manager.getAll().size() == 2, "rejections do not change the book");

        HealthProfessional offDuty =
                new Nurse("008", "Off", EnumSet.of(DayOfWeek.TUESDAY), "Registered nurse");
        check(manager.book("Cara", "0412345680", "11:00", offDuty).startsWith("WARNING"),
                "non-working day is rejected");

        check(manager.byProfessional("001").size() == 1, "filter by professional id");
        check(manager.byPhone("0412345679").size() == 1, 
        "filter by phone");
        check(manager.byProfessional("999").isEmpty(), "unknown professional");
        check(manager.byPhone("0499999999").isEmpty(), "unknown phone");

        check(manager.book("Ada", "0412345678", "11:00", gp).startsWith("OK"),
                "second booking for the same patient");
        List<Appointment> sorted = manager.sorted(manager.getAll());
        check(sorted.get(0).getTime().equals(LocalTime.of(10, 0)),
                "sorted earliest first");
        check(sorted.get(2).getTime().equals(LocalTime.of(11, 0)),
                "sorted latest last");
        check(manager.sorted(manager.byPhone("0412345678")).get(0).getTime()
                .equals(LocalTime.of(10, 0)), "sort a filtered subset");

        List<Appointment> returned = manager.getAll();
        returned.clear();
        check(manager.getAll().size() == 3, "getAll returns a copy");

        check(manager.cancel("002", "10:00").startsWith("OK"),
                "cancel by id and time");
        check(manager.cancel("002", "10:00").startsWith("WARNING"),
                "cancelling twice warns");
        check(manager.cancel("abc", "10:00").startsWith("WARNING"),
                "cancel checks the id");
        check(manager.cancel("004", "later").startsWith("WARNING"),
                "cancel checks the time");
        check(manager.cancelByPhone("0412345678").startsWith("OK"),
                "cancel by phone");
        check(manager.byPhone("0412345678").isEmpty(),
                "phone cancellation removes all matches");
        check(manager.cancelByPhone("0412345678").startsWith("WARNING"),
                "unknown phone warns");
        check(manager.book("Ben", "0412345679", "10:00", nurse).startsWith("OK"),
                "cancelled slot can be reused");

        ClinicSchedule onlyNine = new ClinicSchedule(List.of(LocalTime.of(9, 0)));
        AppointmentManager custom = new AppointmentManager(monday, onlyNine);
        check(custom.book("Ada", "0412345678", "09:00", gp).startsWith("OK"),
                "custom schedule accepts its slot");
        check(custom.book("Ben", "0412345679", "10:00", gp).startsWith("WARNING"),
                "custom schedule rejects other slots");
        mustReject(() -> new AppointmentManager(monday, null), "schedule is required");
        mustReject(() -> new AppointmentManager(null), "day is required");

        System.out.println("PASS: " + checks + " checks");
    }
}
