package bloodbank.rest.resource;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static bloodbank.utility.MyConstants.*;

@Path( "phone")
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class PhoneResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodBankService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getPhones() {
        LOG.debug( "retrieving all phones ...");
        List<Phone> phones = service.getAllPhones();
        Response response = Response.ok( phones).build();
        return response;
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @Path( RESOURCE_PATH_ID_PATH)
    public Response getPhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific phone " + id);
        Phone phone = service.getPhoneWithId(id);
        return Response.ok(phone).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addPhone(Phone phone) {
        LOG.debug( "add a new phone ...");
        Phone phone1 = service.persistPhone(phone);
        return Response.ok(phone1).build();

    }

    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @PUT
    @Path( "/{id}")
    public Response updatePhone( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id, Phone updatingPhone) {
        LOG.debug( "update a specific phone ...");
        Response response = null;
        Phone updatedPhone = service.updatePhone(id, updatingPhone);
        return Response.ok(updatedPhone).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deletePhone(@PathParam("id") int id){
        Response response = null;
        Phone phone = service.getWithId(Phone.class, Phone_.id, id);
        service.deletePhoneById(id);
        return Response.ok(phone).build();
    }
}
