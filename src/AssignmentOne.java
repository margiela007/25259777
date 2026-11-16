import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/**
 * Scripted demonstration of the appointment system.
 *
 * <p>Parts 1-3 create the professionals and show them through the common
 * {@link HealthProfessional} type. Parts 4-6 exercise the appointment model:
 * adding, viewing, filtering, sorting, cancelling, error cases and rebooking.
 * Bookings are always for the current day, as required by the brief.</p>
 */
public class AssignmentOne {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        DayOfWeek todayName = today.getDayOfWeek();

        System.out.println("=== PARTS 1-3: HEALTH PROFESSIONALS ===");
        HealthProfessional[] team = {
            new GeneralPractitioner("101", "Dr Ada Green", true),
            new GeneralPractitioner("102", "Dr Ben Brown", false),
            new GeneralPractitioner("103", "Dr Cara White"),
            new Cardiologist("201", "Dr Dan Lee", "heart rhythm"),
            new Cardiologist("202", "Dr Eve Chen"),
            new Physiotherapist("301", "Sam Taylor", 8),
            new Physiotherapist("302", "Pat Morgan"),
            new Nurse("401", "Alex Jones", "Registered nurse"),
            new Nurse("402", "Jo Smith")
        };
        for (HealthProfessional professional : team) {
            System.out.println(professional);
            System.out.println("    Service: " + professional.service());
        }

        AppointmentManager manager = new AppointmentManager(today);
        System.out.println("\n=== PARTS 4-6: APPOINTMENTS ===");
        System.out.println("Day: " + today + " (" + todayName + ")");
        System.out.println("Schedule: " + manager.getSchedule());

        show("Empty book", manager.getDay(), manager.getAll());
        System.out.println(manager.book("Ada", "0412345678", "09:00", team[0]));
        System.out.println(manager.book("Ben", "0412345679", "10:00", team[3]));
        System.out.println(manager.book("Cara", "0412345680", "11:30", team[5]));
        System.out.println(manager.book("Dee", "0412345681", "14:00", team[7]));
        System.out.println(manager.book("Fay", "0412345682", "10:00", team[8]));
        System.out.println(manager.book("Ada", "0412345678", "15:00", team[0]));
        System.out.println(manager.book("Eli", "0412345683", "16:00", team[1]));

        show("All, insertion order", manager.getDay(), manager.getAll());
        show("All, time order", manager.getDay(), manager.sorted(manager.getAll()));
        show("Professional 101", manager.getDay(), manager.byProfessional("101"));
        show("Professional 101, time order", manager.getDay(),
                manager.sorted(manager.byProfessional("101")));
        show("Patient 0412345678", manager.getDay(), manager.byPhone("0412345678"));
        show("Patient 0412345678, time order", manager.getDay(),
                manager.sorted(manager.byPhone("0412345678")));
        show("Unknown professional", manager.getDay(), manager.byProfessional("999"));
        show("Unknown patient", manager.getDay(), manager.byPhone("0499999999"));

        HealthProfessional offDuty =
                new Nurse("902", "Nina Night", EnumSet.of(todayName.plus(1)), "Enrolled nurse");
        System.out.println("\n=== REJECTED OPERATIONS ===");
        System.out.println(manager.book("Ada", "0412345678", "09:00", team[0]));
        System.out.println(manager.book("Ada", "0412345678", "09:15", team[0]));
        System.out.println(manager.book("Ada", "0412345678", "17:00", team[0]));
        System.out.println(manager.book("", "0412345678", "12:00", team[0]));
        System.out.println(manager.book("Ada", "not-a-phone", "12:00", team[0]));
        System.out.println(manager.book("Ada", "0412345678", "12:00", null));
        System.out.println(manager.book("Ada", "0412345678", "13:00", offDuty));

        System.out.println("\n=== CANCELLATIONS ===");
        System.out.println(manager.cancel("201", "10:00"));
        System.out.println(manager.cancel("201", "10:00"));
        System.out.println(manager.cancelByPhone("0412345678"));
        System.out.println(manager.cancelByPhone("0412345678"));
        show("After cancellations, time order", manager.getDay(),
                manager.sorted(manager.getAll()));
        System.out.println(manager.book("Ben", "0412345679", "10:00", team[3]));
        show("Final book, time order", manager.getDay(), manager.sorted(manager.getAll()));
    }

    /**
     * Prints one labelled view of the appointment list.
     *
     * @param title        the heading for this view
     * @param day          the day the manager covers
     * @param appointments the appointments to print
     */
    private static void show(String title, LocalDate day, List<Appointment> appointments) {
        System.out.println("\n--- " + title + " | " + day + " ---");
        if (appointments.isEmpty()) {
            System.out.println("(no appointments)");
            return;
        }
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }
}
