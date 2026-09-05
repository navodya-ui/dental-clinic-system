package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.dao.DentistDAO;

/**
 * REST endpoint exposing the dentist list, used to populate the
 * dentist dropdown on the Register Appointment screen instead of
 * requiring staff to type a raw dentist_id from memory.
 *
 * Base path: /api/dentists
 */
@Path("/dentists")
@Produces(MediaType.APPLICATION_JSON)
public class DentistResource {

    private final DentistDAO dentistDAO = new DentistDAO();

    @GET
    public Response getAllDentists() {
        return Response.ok(dentistDAO.findAll()).build();
    }
}