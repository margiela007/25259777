# Medical Appointment System

## 1. How to Run

Requirements: JDK 26 (per course announcement), VS Code with the Extension Pack for Java. The project does not depend on Maven/Gradle or any third-party libraries. Verified locally using JDK 26.

Run the following from the project root directory:

```bat
javac -encoding UTF-8 -Xlint:all -d bin src\*.java
java -cp bin AssignmentOne

Run self-tests:
javac -encoding UTF-8 -Xlint:all -d bin src\*.java tests\SelfTest.java
java -cp bin SelfTest
Medical Appointment System
 
1. How to Run
 
Requirements: JDK 26 (per course announcement), VS Code with the Extension Pack for Java. The project does not depend on Maven/Gradle or any third-party libraries. Locally verified using JDK 26.
 
Execute in the project root directory:
javac -encoding UTF-8 -Xlint:all -d bin src\*.java
java -cp bin AssignmentOne
 
Run self-test:
javac -encoding UTF-8 -Xlint:all -d bin src\*.java tests\SelfTest.java
java -cp bin SelfTest
 
Alternatively, double-click run.cmd / test.cmd.
 
Expected: The main program prints 9 health professionals, the add/remove/query/sort operations for 7 appointments, and 7 types of error operations; the self-test finally prints PASS: 61 checks. SAMPLE_OUTPUT.txt is a real output from 2026-11-15; the dates will vary depending on the run date.
 
2. Classes and Responsibilities
File Responsibility
AssignmentOne.java Entry point; Parts 1–3 display personnel, Parts 4–6 demonstrate the complete appointment process; responsible only for printing
HealthProfessional.java Abstract superclass: ID, name, working days, equals/hashCode, toString template
GeneralPractitioner.java General Practitioner: whether accepting new patients
Cardiologist.java Cardiologist: sub-specialty direction
Physiotherapist.java Physiotherapist: years of experience
Nurse.java Nurse: registration level
Patient.java Patient name and mobile phone number (immutable)
Appointment.java Patient + Professional + Time; Comparable sorted by time
ClinicSchedule.java Collection of fixed bookable time slots for the clinic
AppointmentManager.java All appointments for a day: add, query, filter, sort, cancel, conflict check
Check.java Shared validation: non-empty text, numeric ID, mobile phone number
tests/SelfTest.java Regression checks without third-party frameworks

 
3. Locations of the Four Pillars in the Code
 
Encapsulation
• id/name/workingDays in HealthProfessional are all private final and can only be accessed through constructors and getters.
• Check validates upon construction, so objects cannot exist with an empty name or invalid ID.
• getWorkingDays() returns Collections.unmodifiableSet(...); AppointmentManager.getAll() returns new ArrayList<>(...); the time slot list in ClinicSchedule is an unmodifiableList. External code cannot access internal collections, thus object states cannot be modified. 
Inheritance
• The four subclasses extends HealthProfessional, and their constructors use super(id, name[, days]) to reuse ID, name, working days, and validation.
• toString() defines the template in the superclass; subclasses only supplement role()/service()/extra(), avoiding duplication.
• Subclasses also demonstrate constructor overloading: e.g., GeneralPractitioner(id, name), (id, name, accepting), (id, name, days, accepting). 
Polymorphism
• AssignmentOne uses HealthProfessional[] team to store 9 objects of different subclasses and prints them uniformly in one loop; role(), service(), extra() are bound to their respective subclasses at runtime.
• The field type of Appointment is HealthProfessional, so one appointment class can serve any personnel type, eliminating the need to write an appointment class for each type. 
Abstraction
• HealthProfessional is an abstract class and cannot be new HealthProfessional(...), structurally prohibiting "ordinary personnel without a specialty".
• role(), service(), extra() are abstract methods, forcing each subclass to provide its own implementation. 
4. Key Design Decisions
1. Why extract time slots independently into ClinicSchedule?
Part 5 of the Brief requires that "time slots can be changed without affecting the rest of the appointment logic". By extracting time slots, the manager only asks schedule.has(time); changing to another time slot list automatically changes validation, conflict, and demonstration; it also conforms to the Single Responsibility Principle.
2. Why does equals only look at ID?
"The same health professional" should be determined by identity rather than name; different objects of the same ID, or even objects of different subclasses, should be considered the same person, so that duplicate appointment checks are reliable. hashCode is kept consistent with equals.
3. Why is the default constructor "working all week"?
This is for demonstration convenience: the main program can successfully book on any day it runs. Real scheduling uses a constructor with Set<DayOfWeek>; Nina Night in the demo only works "tomorrow" to demonstrate rejection on non-working days. This trade-off needs to be clearly explained in comments and the video.
4. Is 16:00 considered bookable?
This implementation treats 16:00 as the last bookable start time, totaling 17 time slots. If the clinic closes at 16:00, change the loop in ClinicSchedule.standard() to < 16 * 60, and the last slot would be 15:30. Both are justifiable; the key is to state the assumption clearly.
5. Does canceling by mobile phone number cancel one or all?
The Brief does not specify. This implementation deletes all appointments for that mobile phone number on that day and reports how many were deleted; the Javadoc of cancelByPhone specifies this strategy.
6. How to sort the same time with different personnel?
The Brief allows any order. A secondary key (personnel ID) is added in compareTo to make the output stable, reproducible, and easy to test.
7. Why doesn't the manager print?
AppointmentManager only returns "OK: ..." / "WARNING: ..." strings; printing is centralized in AssignmentOne.show(...). After separating the model and the interface, the same manager can be directly swapped for a GUI or used in tests.
8. Why use LocalTime instead of strings to store time?
Sorting, equality checks, and format validation are handled by java.time; input still uses HH:mm text, uniformly parsed by parseTime and rejecting illegal values. 
5. Corresponding Grading Criteria
Grading Item Code Location
Part 1 Modeling Health Professionals HealthProfessional + Check
Part 2 Specialization of Four Types GeneralPractitioner / Cardiologist / Physiotherapist / Nurse
Part 3 Unified Processing of Personnel Objects The team array and loop in AssignmentOne
Part 4 Appointment Modeling Patient, Appointment (including Comparable)
Part 5 Appointment Management ClinicSchedule, AppointmentManager
Part 6 End-to-End Demonstration AssignmentOne.main (Normal + Error + Two Filters + Two Cancellations + Rebooking)

AI Declaration
I declare that AI tools (such as ChatGPT/DeepSeek) were used to assist in revise this  document and debugging the code. All code logic has been reviewed and understood.