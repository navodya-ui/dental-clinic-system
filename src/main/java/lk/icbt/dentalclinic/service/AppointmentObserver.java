package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;

/**
 * Observer pattern: any class that wants to be notified when an
 * appointment event occurs (created, billed, cancelled) implements this.
 * Examples: a UI panel refreshing its table, a console/SMS notifier,
 * an audit logger.
 */
public interface AppointmentObserver {

    void onAppointmentRegistered(Appointment appointment);

    void onBillGenerated(Appointment appointment, String receiptSummary);
}