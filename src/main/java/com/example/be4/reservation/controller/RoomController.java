package com.example.be4.reservation.controller;

import com.example.be4.reservation.dto.ReservationRequest;
import com.example.be4.reservation.entity.Reservation;
import com.example.be4.reservation.entity.Room;
import com.example.be4.reservation.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
@Validated
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoom(id));
    }

    @GetMapping("/room/{id}/reservations")
    public ResponseEntity<List<Reservation>> getRoomReservations(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomReservations(id));
    }

    @GetMapping("/room/{id}/check")
    public ResponseEntity<String> checkReservation(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Long teamId,
            @RequestParam(required = false) Long reservationId) {
        
        try {
            roomService.validateReservation(id, teamId, date, startTime, endTime, reservationId);
            return ResponseEntity.ok().body("예약 가능한 시간입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/room/{id}")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(roomService.createReservation(id, request));
    }

    @DeleteMapping("/room/{roomId}/reservation/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long roomId,
            @PathVariable Long reservationId,
            @RequestParam @NotBlank(message = "비밀번호를 입력해주세요")
            @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다") String password) {

        roomService.deleteReservation(roomId, reservationId, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/room/{roomId}/reservation/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long roomId,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(roomService.updateReservation(roomId, reservationId, request));
    }
}
