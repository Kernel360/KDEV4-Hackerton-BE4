package com.example.be4.reservation.service;

import com.example.be4.reservation.dto.ReservationRequest;
import com.example.be4.reservation.entity.Reservation;
import com.example.be4.reservation.entity.Room;
import com.example.be4.reservation.entity.Team;
import com.example.be4.reservation.repository.ReservationRepository;
import com.example.be4.reservation.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));
    }

    public List<Reservation> getRoomReservations(Long roomId) {
        Room room = getRoom(roomId);
        // 예약 정보와 함께 Team 정보도 한 번에 가져오기
        return reservationRepository.findByRoomOrderByReservationDateAscStartTimeAsc(room);
    }

    public void validateReservation(Long roomId, Long teamId, String date, String startTime, String endTime, Long reservationId) {
        System.out.println("Validating reservation - Room: " + roomId + ", Team: " + teamId + ", Date: " + date + ", Time: " + startTime + "-" + endTime + ", ReservationId: " + reservationId);
        
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        
        // 운영 시간 체크 (09:00-20:00)
        if (start.isBefore(LocalTime.of(9, 0)) || end.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("예약은 오전 9시부터 오후 8시까지만 가능합니다");
        }

        // 2시간 제한 체크 (여유 1분 추가)
        long minutes = java.time.Duration.between(start, end).toMinutes();
        if (minutes > 121) {
            throw new IllegalArgumentException("한 번에 최대 2시간까지만 예약할 수 있습니다");
        }

        // 시간 중복 체크
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                roomId,
                java.time.LocalDate.parse(date),
                start,
                end
        );

        System.out.println("Found " + conflicts.size() + " conflicting reservations");
        if (!conflicts.isEmpty() && (reservationId == null || conflicts.stream().noneMatch(r -> r.getId().equals(reservationId)))) {
            throw new IllegalArgumentException("이미 예약된 시간대입니다");
        }

        // 팀별 하루 총 예약 시간 체크
        List<Reservation> teamReservations = reservationRepository.findByTeamIdAndReservationDate(
                teamId,
                java.time.LocalDate.parse(date)
        );

        int totalMinutes = teamReservations.stream()
                .filter(r -> reservationId == null || !r.getId().equals(reservationId)) // 수정하는 경우 기존 예약 제외
                .mapToInt(r -> {
                    int startMinutes = r.getStartTime().getHour() * 60 + r.getStartTime().getMinute();
                    int endMinutes = r.getEndTime().getHour() * 60 + r.getEndTime().getMinute();
                    return endMinutes - startMinutes;
                })
                .sum();

        int newReservationMinutes = (end.getHour() * 60 + end.getMinute()) - (start.getHour() * 60 + start.getMinute());
        
        System.out.println("Team total minutes: " + totalMinutes + ", New reservation minutes: " + newReservationMinutes);
        if (totalMinutes + newReservationMinutes > 121) {
            throw new IllegalArgumentException("팀별로 하루에 최대 2시간까지만 예약할 수 있습니다");
        }
    }

    @Transactional
    public Reservation createReservation(Long roomId, ReservationRequest request) {
        System.out.println("Creating reservation - RoomId: " + roomId);
        System.out.println("Request data: " + request.toString());

        try {
            // 입력값 검증
            if (request.getTeamId() == null) {
                throw new IllegalArgumentException("팀을 선택해주세요");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호를 입력해주세요");
            }

            // Validate business rules
            validateReservation(roomId, request.getTeamId(), 
                request.getReservationDate().toString(),
                request.getStartTime().toString(),
                request.getEndTime().toString(),
                null);

            Room room = getRoom(roomId);

            Reservation reservation = new Reservation();
            reservation.setRoom(room);
            Team team = new Team();
            team.setId(request.getTeamId());
            reservation.setTeam(team);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setStartTime(request.getStartTime());
            reservation.setEndTime(request.getEndTime());
            reservation.setPassword(request.getPassword());

            Reservation savedReservation = reservationRepository.save(reservation);
            System.out.println("Reservation created successfully: " + savedReservation.getId());
            return savedReservation;
        } catch (Exception e) {
            System.err.println("예약 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("예약 생성 실패: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteReservation(Long roomId, Long reservationId, String password) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (!reservation.getRoom().getId().equals(roomId)) {
            throw new IllegalArgumentException("Reservation does not belong to the specified room");
        }

        if (!reservation.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        reservationRepository.delete(reservation);
    }

    @Transactional
    public Reservation updateReservation(Long roomId, Long reservationId, ReservationRequest request) {
        System.out.println("Updating reservation - RoomId: " + roomId + ", ReservationId: " + reservationId);
        System.out.println("Request data: " + request.toString());

        // 예약 찾기
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다"));

        // 회의실 확인
        if (!reservation.getRoom().getId().equals(roomId)) {
            throw new IllegalArgumentException("해당 회의실의 예약이 아닙니다");
        }

        // 비밀번호 확인
        if (!reservation.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다");
        }

        try {
            // 예약 가능 여부 검증
            validateReservation(
                roomId,
                request.getTeamId(),
                request.getReservationDate().toString(),
                request.getStartTime().toString(),
                request.getEndTime().toString(),
                reservationId
            );

            // 예약 정보 업데이트
            Team team = new Team();
            team.setId(request.getTeamId());
            reservation.setTeam(team);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setStartTime(request.getStartTime());
            reservation.setEndTime(request.getEndTime());
            
            Reservation updatedReservation = reservationRepository.save(reservation);
            System.out.println("Reservation updated successfully: " + updatedReservation.getId());
            return updatedReservation;
        } catch (Exception e) {
            System.err.println("예약 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("예약 수정 실패: " + e.getMessage());
        }
    }
}
