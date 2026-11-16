import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The fixed set of start times the clinic offers.
 *
 * <p>Extracting the schedule into its own class keeps the manager focused on
 * bookings and makes the schedule replaceable: pass a different list of slots
 * and every rule follows the new list automatically.</p>
 */
public final class ClinicSchedule {
    private final List<LocalTime> slots;

    /**
     * @param slots the offered start times; copied and sorted
     * @throws IllegalArgumentException when the list is null or empty, when a
     *                                  slot is null, has seconds/nanoseconds,
     *                                  or is duplicated
     */
    public ClinicSchedule(List<LocalTime> slots) {
        if (slots == null || slots.isEmpty()) {
            throw new IllegalArgumentException("A schedule needs at least one time slot.");
        }
        ArrayList<LocalTime> copy = new ArrayList<>();
        for (LocalTime slot : slots) {
            if (slot == null) {
                throw new IllegalArgumentException("Time slots must not be null.");
            }
            if (slot.getSecond() != 0 || slot.getNano() != 0) {
                throw new IllegalArgumentException("Time slots must be whole minutes: " + slot);
            }
            if (copy.contains(slot)) {
                throw new IllegalArgumentException("Duplicate time slot: " + slot);
            }
            copy.add(slot);
        }
        Collections.sort(copy);
        this.slots = Collections.unmodifiableList(copy);
    }

    /**
     * The standard clinic day: 08:00 to 16:00 every 30 minutes.
     *
     * <p>This treats 16:00 as the last bookable start time, giving 17 slots.
     * If the clinic closes at 16:00, change the loop to {@code < 16 * 60} and
     * the last start becomes 15:30.</p>
     *
     * @return the standard schedule
     */
    public static ClinicSchedule standard() {
        ArrayList<LocalTime> slots = new ArrayList<>();
        for (int minutes = 8 * 60; minutes <= 16 * 60; minutes += 30) {
            slots.add(LocalTime.of(minutes / 60, minutes % 60));
        }
        return new ClinicSchedule(slots);
    }

    /**
     * @param time the time to test
     * @return true when the clinic offers that exact start time
     */
    public boolean has(LocalTime time) {
        return slots.contains(time);
    }

    /** @return a read-only view of the slots, earliest first */
    public List<LocalTime> getSlots() {
        return slots;
    }

    @Override
    public String toString() {
        return slots.toString();
    }
}
