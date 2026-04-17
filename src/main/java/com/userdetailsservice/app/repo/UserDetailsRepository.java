package com.userdetailsservice.app.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.userdetailsservice.app.model.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

	public Page<UserDetails> findAll(Pageable page);
}
