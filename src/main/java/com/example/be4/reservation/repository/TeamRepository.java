package com.example.be4.reservation.repository;

import com.example.be4.reservation.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    // 팀 이름으로 팀을 조회
    Team findByTeamName(String teamName);
}