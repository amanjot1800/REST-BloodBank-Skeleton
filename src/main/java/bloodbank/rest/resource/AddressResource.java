package bloodbank.rest.resource;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Address;

import bloodbank.entity.BloodBank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static bloodbank.utility.MyConstants.*;

@Path( CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class AddressResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodBankService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllAddress() {
        LOG.debug( "retrieving all addresses ...");
        List<Address> Addresses = service.getAllAddresses();
        return Response.ok( Addresses).build();
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @Path( RESOURCE_PATH_ID_PATH)
    public Response getAddressById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific address " + id);
        Address address = service.getAddressWithId(id);
        return Response.ok(address).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addAddress( Address address) {
        Response response = null;
        Address newAddress = service.persistAddress(address);
        response = Response.ok( newAddress).build();
        return response;
    }

    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @PUT
    @Path( "/{id}")
    public Response updateAddress( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id, Address updatingAddress) {
        LOG.debug( "update a specific BloodBank ...");
        Response response = null;
        Address updatedAddress = service.updateAddress(id, updatingAddress);
        return Response.ok(updatedAddress).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteAddress(@PathParam("id") int id){
        Response response = null;

        Address newAddress = service.getAddressWithId(id);
        service.deleteAddress(id);
        return Response.ok(newAddress).build();
    }
}
