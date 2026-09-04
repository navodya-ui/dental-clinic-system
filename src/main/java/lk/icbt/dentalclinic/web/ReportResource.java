package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.dao.ReportDAO;

import java.time.LocalDate;

/**
 * REST endpoints exposing the reports defined in ReportDAO.
 * These are the "suitable set of reports which add more value"
 * requested in Task B.
 *
 * Base path: /api/reports
 */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    private final ReportDAO reportDAO = new ReportDAO();

    /** GET /api/reports/daily?date=2026-09-10 (defaults to today if omitted) */
    @GET
    @Path("/daily")
    public Response getDailyAppointments(@QueryParam("date") String date) {
        LocalDate targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return Response.ok(reportDAO.getDailyAppointments(targetDate)).build();
    }

    /** GET /api/reports/dentist/{dentistId}/schedule?date=2026-09-10 */
    @GET
    @Path("/dentist/{dentistId}/schedule")
    public Response getDentistSchedule(@PathParam("dentistId") int dentistId,
                                        @QueryParam("date") String date) {
        LocalDate targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return Response.ok(reportDAO.getDentistSchedule(dentistId, targetDate)).build();
    }

    /** GET /api/reports/revenue?start=2026-09-01&end=2026-09-30 */
    @GET
    @Path("/revenue")
    public Response getRevenueSummary(@QueryParam("start") String start, @QueryParam("end") String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return Response.ok(reportDAO.getRevenueSummary(startDate, endDate)).build();
    }

    /** GET /api/reports/popular-treatments */
    @GET
    @Path("/popular-treatments")
    public Response getMostRequestedTreatments() {
        return Response.ok(reportDAO.getMostRequestedTreatments()).build();
    }
}