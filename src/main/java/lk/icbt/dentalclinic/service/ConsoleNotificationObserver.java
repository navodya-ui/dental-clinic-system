package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;

/**
 * Concrete Observer: logs appointment events to the console.
 * Later this could be swapped for an SMS/email notifier without
 * changing AppointmentService at all - that's the point of the pattern.
 */
public class ConsoleNotificationObserver implements AppointmentObserver {

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        System.out.println("[NOTIFY] New appointment registered: "
                + appointment.getAppointmentNo() + " for " + appointment.getPatient().getName());
    }

    @Override
    public void onBillGenerated(Appointment appointment, String receiptSummary) {
        System.out.println("[NOTIFY] Bill generated for appointment "
                + appointment.getAppointmentNo() + ":\n" + receiptSummary);
    }
}