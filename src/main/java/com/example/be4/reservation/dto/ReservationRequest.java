package com.example.be4.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservationRequest {
    @NotNull(message = "팀을 선택해주세요")
    private Long teamId;
    
    @NotNull(message = "예약 날짜를 입력해주세요")
    private LocalDate reservationDate;
    
    @NotNull(message = "시작 시간을 입력해주세요")
    private LocalTime startTime;
    
    @NotNull(message = "종료 시간을 입력해주세요")
    private LocalTime endTime;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다")
    private String password;
}
