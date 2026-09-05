package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.dao.TreatmentDAO;

/**
 * REST endpoint exposing the treatment list, used to populate the
 * treatment dropdown on the Register Appointment screen.
 *
 * Base path: /api/treatments
 */
@Path("/treatments")
@Produces(MediaType.APPLICATION_JSON)
public class TreatmentResource {

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @GET
    public Response getAllTreatments() {
        return Response.ok(treatmentDAO.findAll()).build();
    }
}