package com.bookingapi.bookingapi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByPropertyIdAndStatus(@Param("propertyId") Long propertyId,
			@Param("status") BookingStatus status);

	@Query(value = "SELECT b FROM Booking b where b.property.id = :idProperty and b.status IN ('ACTIVE','BLOCKED') and (:startDate between b.startDate and b.endDate OR :endDate between b.startDate and b.endDate)")
	List<Booking> findBlocksFromPropertyBetweenDates(@Param("idProperty") Long idProperty,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	@Query(value = "SELECT b FROM Booking b where b.property.id = :idProperty and b.status IN :status and (:startDate between b.startDate and b.endDate OR :endDate between b.startDate and b.endDate)")
	List<Booking> findBookingsFromPropertyBetweenDates(@Param("idProperty") Long idProperty,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") List<BookingStatus> status);
}
