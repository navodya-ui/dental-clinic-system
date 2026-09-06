package lk.icbt.dentalclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bill.calculateTotal() and printReceipt().
 */
@DisplayName("Bill model")
class BillTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient("Nimasha Perera", "Kandy", "0719876543");
        Dentist dentist = new Dentist(2, "Anusha Fernando", "Orthodontics");
        Treatment treatment = new Treatment(3, "Root Canal", new BigDecimal("8000.00"));

        appointment = new Appointment("APT0010", LocalDate.now().plusDays(2),
                LocalTime.of(11, 0), patient, dentist, treatment);
    }

    @Test
    @DisplayName("calculateTotal() returns the treatment's consultation fee")
    void calculateTotal_returnsTreatmentFee() {
        Bill bill = new Bill(appointment);

        BigDecimal total = bill.calculateTotal();

        assertEquals(0, new BigDecimal("8000.00").compareTo(total));
        assertEquals(0, new BigDecimal("8000.00").compareTo(bill.getTotalAmount()));
    }

    @Test
    @DisplayName("calculateTotal() throws when appointment has no treatment")
    void calculateTotal_missingTreatment_throwsException() {
        appointment.setTreatment(null);
        Bill bill = new Bill(appointment);

        assertThrows(IllegalStateException.class, bill::calculateTotal);
    }

    @Test
    @DisplayName("printReceipt() includes patient name, treatment, and total amount")
    void printReceipt_containsExpectedFields() {
        Bill bill = new Bill(appointment);
        bill.setBillId(101);
        bill.calculateTotal();

        String receipt = bill.printReceipt();

        assertTrue(receipt.contains("APT0010"));
        assertTrue(receipt.contains("Nimasha Perera"));
        assertTrue(receipt.contains("Root Canal"));
        assertTrue(receipt.contains("8000.00"));
    }
}