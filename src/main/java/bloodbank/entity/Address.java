package bloodbank.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;

/**
 * The persistent class for the address database table.
 */

@Entity
@Table( name = "address")
@NamedQuery( name = "Address.findAll", query = "SELECT a FROM Address a left join fetch a.contacts")
@NamedQuery( name = "Address.findWithId", query = "SELECT a FROM Address a left join fetch a.contacts where a.id = :param1")
@AttributeOverride( name = "id", column = @Column( name = "address_id"))
public class Address extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_ADDRESS_QUERY = "Address.findAll";
	public static final String FIND_ADDRESS_ID_QUERY = "Address.findWithId";


	@Basic( optional = false)
	@Column( name = "street_number", nullable = false, length = 10)
	private String streetNumber;

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String city;

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String country;

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String province;

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String street;

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String zipcode;

	@JsonIgnore
	// Hint - @OneToMany is used to define 1:M relationship between this entity and another.
	// Hint - @OneToMany option cascade can be added to define if changes to this entity should cascade to objects.
	// Hint - @OneToMany option cascade will be ignored if not added, meaning no cascade effect.
	// Hint - @OneToMany option fetch should be lazy to prevent eagerly initialing all the data.
	@OneToMany( cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "address")
	// Hint - @JoinColumn is used to define the columns needed to perform a join on action.
	// Hint - @JoinColumn option insertable is defines Whether the column is included in SQL INSERT.
	// Hint - @JoinColumn option updatable is defines Whether the column is included in SQL INSERT.
//	@JoinColumn( name = "address_id", referencedColumnName = "address_id", insertable = false, updatable = false)
	// Hint - java.util.Set is used as a collection, however List could have been used as well.
	// Hint - java.util.Set will be unique and also possibly can provide better get performance with HashCode.
	private Set< Contact> contacts = new HashSet<>();

	public Address() {
	}

	public String getCity() {
		return city;
	}

	public void setCity( String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry( String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince( String province) {
		this.province = province;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet( String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber( String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode( String zipcode) {
		this.zipcode = zipcode;
	}

	@JsonIgnore
	public Set< Contact> getContacts() {
		return contacts;
	}

	public void setContacts( Set< Contact> contacts) {
		this.contacts = contacts;
	}

	public void setAddress( String streetNumber, String street, String city, String province, String country,
			String zipcode) {
		setStreetNumber( streetNumber);
		setStreet( street);
		setCity( city);
		setProvince( province);
		setCountry( country);
		setZipcode( zipcode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime
				* Objects.hash( getCity(), getCountry(), getProvince(), getStreet(), getStreetNumber(), getZipcode());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		Address other = (Address) obj;
		return Objects.equals( getCity(), other.getCity()) && Objects.equals( getCountry(), other.getCountry())
				&& Objects.equals( getProvince(), other.getProvince())
				&& Objects.equals( getStreet(), other.getStreet())
				&& Objects.equals( getStreetNumber(), other.getStreetNumber())
				&& Objects.equals( getZipcode(), other.getZipcode());
	}

}