package bloodbank.rest.resource;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.*;
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

@Path( "blooddonation")
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodDonationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodBankService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getDonations() {
        LOG.debug( "retrieving all Donations ...");
        List<BloodDonation> donations = service.getallBlooddonations();
        return Response.ok(donations).build();
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE })
    @Path( "/{id}")
    public Response getDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific Blood Donation " + id);
        BloodDonation donation = service.getDonationWithId(id);
        return Response.ok(donation).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addBloodDonation(BloodDonation bloodDonation) {
        LOG.debug( "add a new donation ...");
        BloodDonation bloodDonation1 = service.persistBloodDonation(bloodDonation);
        return Response.ok(bloodDonation1).build();

    }


/*    @RolesAllowed( { ADMIN_ROLE, USER_ROLE })
    @PUT
    @Path( "/{id}")
    public Response updateBloodDonation( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id, BloodDonation updatingBloodDonation) {
        LOG.debug( "update a specific BloodDonation ...");
        Response response = null;
        BloodDonation updatedBloodBank = service.updateBloodDonation(id, updatingBloodDonation);
        return Response.ok(updatedBloodBank).build();
    }*/


    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteBloodDonation(@PathParam("id") int id){
        Response response = null;
        BloodDonation donation = service.getDonationWithId(id);
        service.deleteBloodDonationById(id);
        return Response.ok(donation).build();
    }

}
