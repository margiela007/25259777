/**
 * Small, shared validation helpers.
 *
 * <p>Keeping the rules in one place means every class rejects bad input in the
 * same way, and the exact rule can be changed once.</p>
 */
final class Check {
    private Check() {
        // Utility class: never instantiated.
    }

    /**
     * Trims text and rejects null or blank values.
     *
     * @param value the text to check
     * @param field a human-readable field name used in the error message
     * @return the trimmed text
     * @throws IllegalArgumentException when the value is null or blank
     */
    static String text(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank.");
        }
        return value.trim();
    }

    /**
     * Accepts an identifier made only of digits.
     *
     * <p>It is stored as a String on purpose so that leading zeroes are kept:
     * "001" and "1" are different identifiers.</p>
     *
     * @param value the identifier to check
     * @return the identifier unchanged
     * @throws IllegalArgumentException when it is blank or not all digits
     */
    static String id(String value) {
        String id = text(value, "ID");
        if (!id.matches("[0-9]+")) {
            throw new IllegalArgumentException("ID must contain digits only.");
        }
        return id;
    }

    /**
     * Applies a deliberately simple phone rule: 7 to 15 digits, with an
     * optional leading plus sign. This is a demonstration rule, not real
     * telephone-number validation.
     *
     * @param value the phone number to check
     * @return the phone number unchanged
     * @throws IllegalArgumentException when the value does not match the rule
     */
    static String phone(String value) {
        String phone = text(value, "Phone");
        if (!phone.matches("\\+?[0-9]{7,15}")) {
            throw new IllegalArgumentException(
                    "Phone must be 7-15 digits, optionally starting with +.");
        }
        return phone;
    }
}
