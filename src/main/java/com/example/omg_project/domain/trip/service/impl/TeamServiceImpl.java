package com.example.omg_project.domain.trip.service.impl;

import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.repository.TeamRepository;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    @Override
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public Team findByInviteCode(String inviteCode) {
        return teamRepository.findByInviteCode(inviteCode);
    }

    @Override
    public void addUserToTeam(String inviteCode, Long userId) {
        Team team = teamRepository.findByInviteCode(inviteCode);
        User user = userRepository.findById(userId).orElse(null);

        if (team != null && user != null) {
            team.getUsers().add(user);  // 팀에 사용자 추가
            user.getTeams().add(team);  // 사용자에 팀 추가
            teamRepository.save(team);  // 변경사항 저장
            userRepository.save(user);  // 변경사항 저장
        }
    }

    @Override
    public void removeUserFromTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team != null && user != null) {
            if (team.getLeader().getId().equals(userId)) {
                throw new IllegalStateException("팀 리더는 팀에서 탈퇴할 수 없습니다.");
            }
            team.getUsers().remove(user);
            user.getTeams().remove(team);
            teamRepository.save(team);
            userRepository.save(user);
        }
    }

    @Override
    public List<Map<String, Object>> getUserTeams(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        return user.getTeams().stream()
                .map(team -> {
                    Map<String, Object> teamData = new HashMap<>();
                    teamData.put("id", team.getId());
                    teamData.put("tripName", team.getTrip().getTripName());

                    // 현재 사용자가 팀의 리더인지 확인
                    boolean isLeader = team.getLeader().getId().equals(userId);
                    teamData.put("isLeader", isLeader);

                    return teamData;
                })
                .collect(Collectors.toList());
    }
}


