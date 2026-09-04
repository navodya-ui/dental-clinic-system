package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Bill;
import lk.icbt.dentalclinic.service.AppointmentService;
import lk.icbt.dentalclinic.service.AuditLogObserver;
import lk.icbt.dentalclinic.service.BillService;
import lk.icbt.dentalclinic.service.ConsoleNotificationObserver;

/**
 * REST endpoints for billing operations, mirroring the
 * "Search Appointment & Generate Bill" sequence diagram.
 *
 * Base path: /api/bills
 */
@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillResource {

    private final BillService billService;
    private final AppointmentService appointmentService;

    public BillResource() {
        this.billService = new BillService();
        this.appointmentService = new AppointmentService();
        this.billService.registerObserver(new ConsoleNotificationObserver());
        this.billService.registerObserver(new AuditLogObserver());
    }

    /**
     * POST /api/bills/{appointmentNo}
     * Generates and persists a bill for the given appointment,
     * returning the printable receipt text.
     */
    @POST
    @Path("/{appointmentNo}")
    public Response generateBill(@PathParam("appointmentNo") String appointmentNo) {
        try {
            Appointment appointment = appointmentService.searchAppointment(appointmentNo);
            String receipt = billService.generateBill(appointment);
            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse(receipt)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    /**
     * GET /api/bills/{appointmentNo}
     * Retrieves the bill already generated for an appointment.
     */
    @GET
    @Path("/{appointmentNo}")
    public Response getBill(@PathParam("appointmentNo") String appointmentNo) {
        Bill bill = billService.findBillByAppointmentNo(appointmentNo);
        if (bill == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No bill found for appointment " + appointmentNo)).build();
        }
        return Response.ok(bill).build();
    }
}