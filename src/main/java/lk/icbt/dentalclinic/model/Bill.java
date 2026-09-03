package lk.icbt.dentalclinic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bill {

    private int billId;
    private Appointment appointment;
    private BigDecimal totalAmount;
    private LocalDate billDate;

    public Bill() {
    }

    public Bill(Appointment appointment) {
        this.appointment = appointment;
        this.billDate = LocalDate.now();
    }

    public Bill(int billId, Appointment appointment, BigDecimal totalAmount, LocalDate billDate) {
        this.billId = billId;
        this.appointment = appointment;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    /**
     * Calculates the bill total based on the treatment's consultation fee.
     * Kept simple here; extend later if additional charges apply.
     */
    public BigDecimal calculateTotal() {
        if (appointment == null || appointment.getTreatment() == null) {
            throw new IllegalStateException("Cannot calculate total: appointment or treatment missing");
        }
        this.totalAmount = appointment.getTreatment().getFee();
        return this.totalAmount;
    }

    public String printReceipt() {
        return "----- Sunrise Dental Clinic -----\n"
                + "Bill No: " + billId + "\n"
                + "Date: " + billDate + "\n"
                + "Appointment No: " + appointment.getAppointmentNo() + "\n"
                + "Patient: " + appointment.getPatient().getName() + "\n"
                + "Treatment: " + appointment.getTreatment().getTreatmentType() + "\n"
                + "Total Amount: Rs. " + totalAmount + "\n"
                + "----------------------------------";
    }

    @Override
    public String toString() {
        return printReceipt();
    }
}