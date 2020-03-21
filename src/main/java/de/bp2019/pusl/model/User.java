package de.bp2019.pusl.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.bp2019.pusl.enums.UserType;

/**
 * Model of a User. Implements {@link UserDetails} to be used with Spring
 * Security
 * 
 * @author Leon Chemnitz
 */
@Document
public class User implements UserDetails {
	private static final long serialVersionUID = 1535517480345333837L;

	@Id
	private ObjectId id;

	private String firstName;

	private String lastName;

	private String emailAddress;

	private String password;

	private Set<Institute> institutes;

	private UserType type;

	public User() {
		firstName = "";
		lastName = "";
		emailAddress = "";
		password = "";
		institutes = new HashSet<>();
		type = UserType.HIWI;
	}

	public User(String firstName, String lastName, String emailAddress, String password, Set<Institute> institutes,
			UserType type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.institutes = institutes;
		this.type = type;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority(type.toString()));
	}

	@Override
	public String getUsername() {
		return emailAddress;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String eMail) {
		this.emailAddress = eMail;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Institute> getInstitutes() {
		return institutes;
	}

	public void setInstitutes(Set<Institute> institutes) {
		this.institutes = institutes;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList("firstName", "lastName", "emailAddress", "password", "institutes", "type"));
	}
}
