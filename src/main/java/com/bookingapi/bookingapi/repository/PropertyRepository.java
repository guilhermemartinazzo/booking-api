package com.bookingapi.bookingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookingapi.bookingapi.model.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Long>{

}
