package com.usermanagement.repositories;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanagement.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmailAddress(String emailAddress);	

}
