package com.example.be4.reservation.repository;

import com.example.be4.reservation.entity.Reservation;
import com.example.be4.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.team t JOIN FETCH r.room rm " +
           "WHERE r.room.id = :roomId AND r.reservationDate = :date " +
           "AND NOT (r.endTime <= :startTime OR r.startTime >= :endTime)")
    List<Reservation> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.team WHERE r.room = :room ORDER BY r.reservationDate ASC, r.startTime ASC")
    List<Reservation> findByRoomOrderByReservationDateAscStartTimeAsc(@Param("room") Room room);

    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.team t JOIN FETCH r.room WHERE r.team.id = :teamId AND r.reservationDate = :date")
    List<Reservation> findByTeamIdAndReservationDate(
            @Param("teamId") Long teamId,
            @Param("date") LocalDate date
    );
}