package com.bookingapi.bookingapi.model.entity;

import java.util.List;

import com.bookingapi.bookingapi.enumerator.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String email;

	@Column
	@Enumerated(EnumType.STRING)
	private UserType userType;

	@OneToMany(mappedBy = "manager")
	private List<Property> managedProperties;

	@OneToMany(mappedBy = "owner")
	private List<Property> ownedProperties;

	public User(Long id) {
		this.id = id;
	}
}
