/**
 * File: RecordService.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.ejb;


import static bloodbank.entity.BloodDonation.FIND_ALL_BLOODDONATION_QUERY;
import static bloodbank.entity.BloodDonation.FIND_ONE_BLOODDONATION_QUERY;
import static bloodbank.entity.Address.FIND_ALL_ADDRESS_QUERY;
import static bloodbank.entity.Address.FIND_ADDRESS_ID_QUERY;
import static bloodbank.entity.BloodBank.ALL_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.Person.ALL_PERSONS_QUERY_NAME;
import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Singleton;
import javax.faces.view.facelets.Facelet;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import bloodbank.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;


/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public <T> List<T> getAll(Class<T> clazz) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        TypedQuery<T> tq = em.createQuery(query);
        return tq.getResultList();
    }


    public List<BloodDonation> getallBlooddonations() {
        TypedQuery<BloodDonation> findAll = em.createNamedQuery(FIND_ALL_BLOODDONATION_QUERY, BloodDonation.class);
        return findAll.getResultList();
    }

    public List<Address> getAllAddresses() {
        TypedQuery<Address> findAll = em.createNamedQuery(FIND_ALL_ADDRESS_QUERY, Address.class);
        return findAll.getResultList();
    }

    public List<Phone> getAllPhones() {
        TypedQuery<Phone> findAll = em.createNamedQuery("Phone.findAll", Phone.class);
        return findAll.getResultList();
    }

    public List<DonationRecord> getAllDonationRecords() {
        TypedQuery<DonationRecord> findAll = em.createNamedQuery("DonationRecord.findAll", DonationRecord.class);
        return findAll.getResultList();
    }

    public <T, R> T getWithId(Class< T> clazz, SingularAttribute<? super T, R> sa, R id) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        query.where(builder.equal(root.get(sa), builder.parameter(Integer.class, "id")));
        TypedQuery<T> tq = em.createQuery( query);
        tq.setParameter( "id", id);
        return tq.getSingleResult();

    }

    public BloodDonation getDonationWithId(int id) {
        TypedQuery<BloodDonation> findAll = em.
                createNamedQuery(FIND_ONE_BLOODDONATION_QUERY, BloodDonation.class)
                .setParameter("param1", id);
        return findAll.getSingleResult();
    }

    public DonationRecord getDonationRecordWithId(int id) {
        TypedQuery<DonationRecord> findDonationRecord = em.
                createNamedQuery("DonationRecord.findById", DonationRecord.class)
                .setParameter("param1", id);
        return findDonationRecord.getSingleResult();
    }

    public Address getAddressWithId(int id) {
        TypedQuery<Address> findAddress = em.
                createNamedQuery(FIND_ADDRESS_ID_QUERY, Address.class)
                .setParameter("param1", id);
        return findAddress.getSingleResult();
    }

    public Phone getPhoneWithId(int id) {
        TypedQuery<Phone> findPhone = em.
                createNamedQuery("Phone.findById", Phone.class)
                .setParameter("param1", id);
        return findPhone.getSingleResult();
    }

    @Transactional
    public Address persistAddress(Address address){
        if( address !=null){
            em.persist(address);
            return getAddressWithId(address.getId());
        }
        return null;
    }


    @Transactional
    public BloodBank persistBloodBank(BloodBank newBank) {
        if (newBank != null){
            em.persist(newBank);
            return getWithId(BloodBank.class, BloodBank_.id, newBank.getId());
        }
        return null;
    }

    @Transactional
    public Boolean isDuplicated(BloodBank bank){
        List<BloodBank> allBanks = getAll(BloodBank.class);
        for (BloodBank bank1: allBanks) {
                if (bank1.getName().equals(bank.getName())){
                    return true;
                }
        }
        return false;
    }

    @Transactional
    public Person persistPerson(Person newPerson) {
        if (newPerson != null){
            em.persist(newPerson);
            return getWithId(Person.class, Person_.id, newPerson.getId());
        }
        return null;
    }

    @Transactional
    public void buildUserForNewPerson(Person newPerson) {
        SecurityUser userForNewPerson = new SecurityUser();
        userForNewPerson.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPerson.setPwHash(pwHash);
        userForNewPerson.setPerson(newPerson);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class)
            .setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewPerson.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPerson);
        em.persist(userForNewPerson);
    }

    @Transactional
    public Person setAddressFor(int id, Address newAddress) {
//        Contact contact = getContactWithID(id);
//        contact.setAddress(newAddress);
//        em.merge(contact);
        return null;
    }

    /**
     * to update a person
     * 
     * @param id - id of entity to update
     * @param personWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Person updatePersonById(int id, Person personWithUpdates) {
        Person personToBeUpdated = getWithId(Person.class, Person_.id, id);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
    }

    /**
     * to delete a person by id
     * 
     * @param id - person id to delete
     */
    @Transactional
    public void deletePersonById(int id) {
        Person person = getWithId(Person.class, Person_.id, id);
        if (person != null) {
            em.refresh(person);
            TypedQuery<SecurityUser> findUser = em
                .createNamedQuery(USER_FOR_OWNING_PERSON_QUERY, SecurityUser.class)
                .setParameter(PARAM1, person.getId());
            /*SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);*/
            em.remove(person);
        }
    }

    @Transactional
    public void deleteBankById(int id) {
        BloodBank bank = getWithId(BloodBank.class, BloodBank_.id, id);
        if (bank != null) {
            em.refresh(bank);
            em.remove(bank);
        }
    }

    @Transactional
    public void deleteBloodDonationById(int id) {
        BloodDonation donation = getWithId(BloodDonation.class, BloodDonation_.id, id);
        if (donation != null) {
            em.refresh(donation);
            em.remove(donation);
        }
    }


    @Transactional
    public void deleteAddress(int id){
        Address address = getWithId(Address.class, Address_.id, id);
        if (address != null) {
            em.refresh(address);
            em.remove(address);
        }
    }

    @Transactional
    public BloodBank updateBloodBank(int bbID, BloodBank newBloodBank) {
        BloodBank banktobeupdated = getWithId(BloodBank.class, BloodBank_.id, bbID);
        em.refresh(banktobeupdated);
        newBloodBank.setId(bbID);
        em.merge(newBloodBank);
        em.flush();
        return getWithId(BloodBank.class, BloodBank_.id, bbID);
    }

    @Transactional
    public BloodDonation updateBloodDonation(int id, BloodDonation newBloodDonation) {
        BloodDonation donation = getWithId(BloodDonation.class, BloodDonation_.id, id);
        em.refresh(donation);
        newBloodDonation.setId(donation.getId());
        em.merge(newBloodDonation);
        em.flush();
        return getWithId(BloodDonation.class, BloodDonation_.id, id);
    }

    @Transactional
    public Address updateAddress(int id, Address address) {
        Address address1 = getAddressWithId(id);
        em.refresh(address1);
        address.setId(address1.getId());
        em.merge(address);
        em.flush();
        return getAddressWithId(id);
    }

    @Transactional
    public Phone updatePhone(int id, Phone phone) {
        Phone phone1 = getPhoneWithId(id);
        em.refresh(phone1);
        phone.setId(phone1.getId());
        em.merge(phone);
        em.flush();
        return getPhoneWithId(id);
    }

    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation bloodDonation) {
        if (bloodDonation != null){
            em.persist(bloodDonation);
            return getWithId(BloodDonation.class, BloodDonation_.id, bloodDonation.getId());
        }
        return null;
    }

    @Transactional
    public DonationRecord persistDonationRecord(DonationRecord donationRecord){
        if (donationRecord != null){
            em.persist(donationRecord);
            return getWithId(DonationRecord.class, DonationRecord_.id, donationRecord.getId());
        }
        return null;
    }

    @Transactional
    public void deleteDonationRecordById(int id){
        DonationRecord donationRecord = getWithId(DonationRecord.class, DonationRecord_.id, id);
        if (donationRecord != null) {
            em.refresh(donationRecord);
            em.remove(donationRecord);
        }
    }

    @Transactional
    public Phone persistPhone(Phone phone){
        if (phone!=null){
            em.persist(phone);
            return getWithId(Phone.class, Phone_.id, phone.getId());
        }
        return null;
    }

    @Transactional
    public void deletePhoneById(int id){
        Phone phone = getWithId(Phone.class, Phone_.id, id);
        if (phone != null) {
            em.refresh(phone);
            em.remove(phone);
        }
    }
}