package com.bookingapi.bookingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookingapi.bookingapi.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
