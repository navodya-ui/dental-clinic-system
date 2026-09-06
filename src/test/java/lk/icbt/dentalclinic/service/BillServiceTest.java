package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.dao.BillDAO;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Bill;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BillService, with BillDAO mocked via Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillService")
class BillServiceTest {

    @Mock
    private BillDAO billDAO;

    private BillService billService;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        billService = new BillService(billDAO);

        Patient patient = new Patient("Kasun Silva", "Colombo", "0771234567");
        Dentist dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(3, "Root Canal", new BigDecimal("8000.00"));
        appointment = new Appointment("APT0001", LocalDate.now().plusDays(1),
                LocalTime.of(10, 30), patient, dentist, treatment);
    }

    @Test
    @DisplayName("generateBill() calculates the correct total and saves the bill")
    void generateBill_savesCorrectTotal() {
        when(billDAO.saveBill(any(Bill.class))).thenReturn(true);

        String receipt = billService.generateBill(appointment);

        ArgumentCaptor<Bill> billCaptor = ArgumentCaptor.forClass(Bill.class);
        verify(billDAO).saveBill(billCaptor.capture());

        assertEquals(0, new BigDecimal("8000.00").compareTo(billCaptor.getValue().getTotalAmount()));
        assertNotNull(receipt);
        assertTrue(receipt.contains("Root Canal"));
    }

    @Test
    @DisplayName("generateBill() throws when the DAO fails to save")
    void generateBill_daoSaveFails_throwsException() {
        when(billDAO.saveBill(any(Bill.class))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> billService.generateBill(appointment));
    }

    @Test
    @DisplayName("generateBill() notifies observers with the appointment and receipt text")
    void generateBill_notifiesObservers() {
        when(billDAO.saveBill(any(Bill.class))).thenReturn(true);
        AppointmentObserver observer = mock(AppointmentObserver.class);
        billService.registerObserver(observer);

        String receipt = billService.generateBill(appointment);

        verify(observer, times(1)).onBillGenerated(eq(appointment), eq(receipt));
    }

    @Test
    @DisplayName("findBillByAppointmentNo() delegates to the DAO and returns its result")
    void findBillByAppointmentNo_delegatesToDao() {
        Bill expected = new Bill(appointment);
        when(billDAO.findByAppointmentNo("APT0001")).thenReturn(expected);

        Bill result = billService.findBillByAppointmentNo("APT0001");

        assertEquals(expected, result);
        verify(billDAO, times(1)).findByAppointmentNo("APT0001");
    }
}