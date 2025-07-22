package com.eaglebank.eagle_bank_api.repository;

import com.eaglebank.eagle_bank_api.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}