package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.service.AppointmentService;
import lk.icbt.dentalclinic.service.ConsoleNotificationObserver;
import lk.icbt.dentalclinic.service.AuditLogObserver;

/**
 * REST endpoints for appointment operations. This class is what makes
 * the system "distributed" - the JavaFX client (or any other client)
 * talks to these endpoints over HTTP instead of calling the service
 * layer directly in-process.
 *
 * Base path: /api/appointments
 */
@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {

    private final AppointmentService appointmentService;

    public AppointmentResource() {
        this.appointmentService = new AppointmentService();
        // wire up Observer pattern listeners for events raised through the API
        this.appointmentService.registerObserver(new ConsoleNotificationObserver());
        this.appointmentService.registerObserver(new AuditLogObserver());
    }

    /**
     * POST /api/appointments
     * Registers a new appointment. Expects a JSON body matching
     * AppointmentRequest (patient/dentist/treatment ids + date/time).
     */
    @POST
    public Response registerAppointment(AppointmentRequest request) {
        try {
            Appointment appointment = request.toAppointment();
            Appointment created = appointmentService.registerAppointment(
                    appointment.getPatient(), appointment.getDentist(), appointment.getTreatment(),
                    appointment.getAppointmentDate(), appointment.getAppointmentTime());
            return Response.status(Response.Status.CREATED).entity(created).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    /**
     * GET /api/appointments/{appointmentNo}
     * Retrieves a single appointment by its business-facing appointment number.
     */
    @GET
    @Path("/{appointmentNo}")
    public Response getAppointment(@PathParam("appointmentNo") String appointmentNo) {
        try {
            Appointment appointment = appointmentService.searchAppointment(appointmentNo);
            return Response.ok(appointment).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    /**
     * PUT /api/appointments/{appointmentNo}/complete
     * Marks an appointment as completed.
     */
    @PUT
    @Path("/{appointmentNo}/complete")
    public Response completeAppointment(@PathParam("appointmentNo") String appointmentNo) {
        try {
            Appointment appointment = appointmentService.searchAppointment(appointmentNo);
            boolean updated = appointmentService.markCompleted(appointment);
            return updated
                    ? Response.ok(new SuccessResponse("Appointment marked as completed")).build()
                    : Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Failed to update appointment status")).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
}