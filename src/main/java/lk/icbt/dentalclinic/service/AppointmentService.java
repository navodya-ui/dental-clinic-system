package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.dao.AppointmentDAO;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic (service) layer - sits between the UI/web-service
 * layer and the DAO layer, matching the sequence diagrams:
 * registerAppointment(), searchAppointment().
 *
 * Uses:
 *  - AppointmentFactory (Factory pattern) to construct new Appointment objects
 *  - AppointmentObserver (Observer pattern) to notify listeners of events
 *  - AppointmentDAO (DAO pattern) to persist/retrieve data
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Constructor overload allowing a DAO to be injected - used by unit tests
     * (with a Mockito mock) so business logic can be tested without a live
     * database connection. Production code uses the no-arg constructor above.
     */
    public AppointmentService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public void registerObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
    }

    private void notifyAppointmentRegistered(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentRegistered(appointment);
        }
    }

    /**
     * Registers a new appointment: builds the object via the Factory,
     * validates it, and persists it via the DAO. Mirrors the
     * "Register New Appointment" sequence diagram.
     */
    public Appointment registerAppointment(Patient patient, Dentist dentist, Treatment treatment,
                                            LocalDate date, LocalTime time) {

        // Built with a null appointment number - AppointmentDAO.addAppointment()
        // derives and assigns the real "APTnnnn" number atomically from the
        // database's own AUTO_INCREMENT id once the row is inserted, avoiding
        // any application-level race condition on the number itself.
        Appointment appointment = AppointmentFactory.createAppointment(
                null, patient, dentist, treatment, date, time);

        if (!appointment.validateAppointment()) {
            throw new IllegalArgumentException("Invalid appointment details - please check the date/time and required fields.");
        }

        appointmentDAO.addAppointment(appointment); // mutates appointment: sets id + appointment number

        notifyAppointmentRegistered(appointment);
        return appointment;
    }

    /**
     * Searches for an appointment by its business-facing appointment number.
     * Mirrors the "Search Appointment" sequence diagram.
     */
    public Appointment searchAppointment(String appointmentNo) {
        Appointment appointment = appointmentDAO.findAppointmentByNo(appointmentNo);
        if (appointment == null) {
            throw new IllegalArgumentException("No appointment found with number: " + appointmentNo);
        }
        return appointment;
    }

    public boolean markCompleted(Appointment appointment) {
        return appointmentDAO.updateStatus(appointment.getAppointmentId(), Appointment.Status.COMPLETED);
    }
}