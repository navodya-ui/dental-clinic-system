package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.dao.BillDAO;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Bill;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic layer for billing - mirrors the "Search Appointment
 * & Generate Bill" sequence diagram (new Bill -> getFee -> calculateTotal
 * -> printReceipt).
 */
public class BillService {

    private final BillDAO billDAO;
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public BillService() {
        this.billDAO = new BillDAO();
    }

    /**
     * Constructor overload allowing a DAO to be injected - used by unit tests
     * (with a Mockito mock) so business logic can be tested without a live
     * database connection. Production code uses the no-arg constructor above.
     */
    public BillService(BillDAO billDAO) {
        this.billDAO = billDAO;
    }

    public void registerObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    private void notifyBillGenerated(Appointment appointment, String receiptSummary) {
        for (AppointmentObserver observer : observers) {
            observer.onBillGenerated(appointment, receiptSummary);
        }
    }

    /**
     * Generates and persists a bill for the given appointment,
     * then returns the printable receipt text.
     */
    public String generateBill(Appointment appointment) {
        Bill bill = new Bill(appointment);
        bill.calculateTotal();

        boolean saved = billDAO.saveBill(bill);
        if (!saved) {
            throw new RuntimeException("Failed to save bill to the database.");
        }

        String receipt = bill.printReceipt();
        notifyBillGenerated(appointment, receipt);
        return receipt;
    }

    public Bill findBillByAppointmentNo(String appointmentNo) {
        return billDAO.findByAppointmentNo(appointmentNo);
    }
}