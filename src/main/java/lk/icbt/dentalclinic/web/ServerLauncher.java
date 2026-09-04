package lk.icbt.dentalclinic.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Boots an embedded Jetty server that hosts the Jersey (JAX-RS) REST API
 * defined in this package. No external application server (Tomcat/GlassFish)
 * or framework like Spring Boot is required - Jetty is started programmatically
 * from plain Java, which is what makes this a genuinely standalone,
 * distributed web-service tier.
 *
 * Run this class to start the API on http://localhost:8080/api/...
 */
public class ServerLauncher {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "jakarta.ws.rs.Application",
                "lk.icbt.dentalclinic.web.ApplicationConfig"
        );

        server.start();
        System.out.println("=================================================");
        System.out.println(" Sunrise Dental Clinic REST API started");
        System.out.println(" Base URL: http://localhost:" + PORT + "/api");
        System.out.println(" Try: POST/GET http://localhost:" + PORT + "/api/appointments");
        System.out.println("=================================================");
        server.join();
    }
}