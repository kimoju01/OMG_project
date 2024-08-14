package com.example.omg_project.domain.user.service;

import com.example.omg_project.domain.user.dto.request.Oauth2LoginDto;
import com.example.omg_project.domain.user.dto.request.UserEditDto;
import com.example.omg_project.domain.user.dto.request.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username); // 아이디 찾기

    void signUp(UserSignUpDto userSignUpDto); // 회원가입

    void deleteUser(Long userId); // 회원 탈퇴

    boolean existsByUsernick(String usernick); // 닉네임 중복 확인

    boolean existsByUsername(String username); // 이메일 중복 확인

    Optional<User> updateUser(String username, UserEditDto userEditDto); // 회원 정보 수정

    Optional<User> getAuthenticatedUser();

    Optional<User> updateOauth2(String username, Oauth2LoginDto oauth2LoginDto); // 소셜로그인 회원 추가 정보 기입

    List<User> findAllUsers(); // 관리자 모든 사용자 조회
}
