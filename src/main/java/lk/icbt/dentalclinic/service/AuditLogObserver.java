package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete Observer: writes every appointment/billing event to a local
 * audit log file. Demonstrates the Observer pattern's key benefit -
 * this class can be added or removed without touching AppointmentService
 * or BillService at all.
 */
public class AuditLogObserver implements AppointmentObserver {

    private static final String LOG_FILE = "audit_log.txt";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        writeLine("APPOINTMENT_CREATED: " + appointment.getAppointmentNo()
                + " for " + appointment.getPatient().getName());
    }

    @Override
    public void onBillGenerated(Appointment appointment, String receiptSummary) {
        writeLine("BILL_GENERATED: appointment " + appointment.getAppointmentNo());
    }

    private void writeLine(String message) {
        String line = "[" + LocalDateTime.now().format(FORMAT) + "] " + message;
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}