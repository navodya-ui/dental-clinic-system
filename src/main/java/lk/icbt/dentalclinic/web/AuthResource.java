package lk.icbt.dentalclinic.web;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.icbt.dentalclinic.dao.UserDAO;
import lk.icbt.dentalclinic.model.User;

/**
 * REST endpoint for the Login use case. Kept deliberately simple
 * (no session tokens/JWT) since the brief only requires that access
 * be restricted to authorized staff, not multi-client session management.
 *
 * Base path: /api/auth
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UserDAO userDAO = new UserDAO();

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        User user = userDAO.findByCredentials(request.username, request.password);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Invalid username or password")).build();
        }

        LoginResponse response = new LoginResponse();
        response.username = user.getUsername();
        response.role = user.getRole().name();
        return Response.ok(response).build();
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class LoginResponse {
        public String username;
        public String role;
    }
}