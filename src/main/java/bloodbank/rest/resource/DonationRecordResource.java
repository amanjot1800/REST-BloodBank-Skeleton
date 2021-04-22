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
    public Response getDonationRecords() {
        LOG.debug( "retrieving all DonationRecords ...");
        List<DonationRecord> donationRecords = service.getAll(DonationRecord.class);
        return Response.ok(donationRecords).build();
    }

    @GET
    @RolesAllowed( { ADMIN_ROLE })
    @Path( RESOURCE_PATH_ID_PATH)
    public Response getDonationRecordById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug( "try to retrieve specific Donation Record " + id);
        DonationRecord donationRecord = service.getDonationRecordWithId(id);
        return Response.ok(donationRecord).build();
    }

    @POST
    @RolesAllowed( { ADMIN_ROLE })
    public Response addDonationRecord(DonationRecord donationRecord) {
        LOG.debug( "add a new donation ...");
        DonationRecord donationRecord1 = service.persistDonationRecord(donationRecord);
        return Response.ok(donationRecord1).build();

    }



    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteDonationRecord(@PathParam("id") int id){
        Response response = null;

        DonationRecord donationRecord = service.getWithId(DonationRecord.class, DonationRecord_.id, id);
        service.deleteDonationRecordById(id);
        return Response.ok(donationRecord).build();
    }
}
