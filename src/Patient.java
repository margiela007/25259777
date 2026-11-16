/**
 * Immutable patient details: the name and mobile number recorded for a
 * booking. There are no medical records here, only what an appointment needs.
 */
public final class Patient {
    private final String name;
    private final String phone;

    /**
     * @param name  the patient's name
     * @param phone the contact number; 7-15 digits, optional leading +
     * @throws IllegalArgumentException when either value is invalid
     */
    public Patient(String name, String phone) {
        this.name = Check.text(name, "Patient name");
        this.phone = Check.phone(phone);
    }

    /** @return the patient's name */
    public String getName() {
        return name;
    }

    /** @return the patient's phone number */
    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
