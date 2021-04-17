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

@Path( "bloodbank")
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodBankResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodBankService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getBloodBanks() {
        LOG.debug( "retrieving all BloodBanks ...");
        List<BloodBank> banks = service.getAll(BloodBank.class);
        return Response.ok(banks).build();
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE })
    @Path( RESOURCE_PATH_ID_PATH)
    public Response getBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific BloodBank " + id);
        BloodBank bank = service.getWithId(BloodBank.class, BloodBank_.id, id);
        return Response.ok(bank).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addBloodBank( BloodBank newBloodBank) {
        LOG.debug( "add a new bloodbank ...");
        if(service.isDuplicated(newBloodBank)) {
            HttpErrorResponse er = new HttpErrorResponse( Response.Status.CONFLICT.getStatusCode(), "entity already exists");
            return Response.status( Response.Status.CONFLICT).entity( er).build();
        }else {
            BloodBank tempBloodBank = service.persistBloodBank(newBloodBank);
            return Response.ok(tempBloodBank).build();
        }
    }

    @RolesAllowed( { ADMIN_ROLE })
    @POST
    @Path("/{id}/blooddonation")
    public Response addBloodDonationToBloodBank( @PathParam("id") int bbID, BloodDonation newBloodDonation) {
        LOG.debug( "add a new BloodDonation to bloodbank={} ...", bbID);

        BloodBank bb = service.getWithId(BloodBank.class, BloodBank_.id, bbID);
        newBloodDonation.setBank(bb);
        bb.getDonations().add(newBloodDonation);
        bb = service.updateBloodBank(bbID, bb);
        return Response.ok( bb).build();
    }

    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @PUT
    @Path( "/{id}")
    public Response updateBloodBank( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id, BloodBank updatingBloodBank) {
        LOG.debug( "update a specific BloodBank ...");
        Response response = null;
        BloodBank updatedBloodBank = service.updateBloodBank( id, updatingBloodBank);
        return Response.ok( updatedBloodBank).build();
    }



    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteBank(@PathParam("id") int id){
        Response response = null;
        BloodBank bank = service.getWithId(BloodBank.class, Person_.id, id);
        service.deleteBankById(id);
        return Response.ok(bank).build();
    }

}
