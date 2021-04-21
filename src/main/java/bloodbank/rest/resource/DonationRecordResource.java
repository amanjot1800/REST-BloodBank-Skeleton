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

@Path( "donationrecord")
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class DonationRecordResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodBankService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getDonations() {
        LOG.debug( "retrieving all DonationRecords ...");
        List<DonationRecord> donationRecords = service.getAll(DonationRecord.class);
        return Response.ok(donationRecords).build();
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE })
    @Path( RESOURCE_PATH_ID_PATH)
    public Response getDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific Donation Record " + id);
        DonationRecord donationRecord = service.getDonationRecordWithId(id);
        return Response.ok(donationRecord).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addBloodDonation(BloodDonation bloodDonation) {
        LOG.debug( "add a new donation ...");
        BloodDonation bloodDonation1 = service.persistBloodDonation(bloodDonation);
        return Response.ok(bloodDonation1).build();

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
