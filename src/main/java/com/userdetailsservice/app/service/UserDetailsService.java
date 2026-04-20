package com.userdetailsservice.app.service;

import java.util.List;

import javax.management.relation.InvalidRelationIdException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.userdetailsservice.app.entityenum.EntityEnum;
import com.userdetailsservice.app.exp.InvalidIdException;
import com.userdetailsservice.app.exp.UserNotFoundException;
import com.userdetailsservice.app.model.ServiceRequests;
import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.repo.ServiceRequestsRepo;
import com.userdetailsservice.app.repo.UserDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsService {

	Logger LOGGER = LoggerFactory.getLogger(UserDetailsService.class);

	private UserDetailsRepository userDetailsRepository;
	private ServiceRequestsRepo serviceRequestsRepo;

	UserDetailsService(UserDetailsRepository userDetailsRepository, ServiceRequestsRepo serviceRequestsRepo) {
		this.userDetailsRepository = userDetailsRepository;
		this.serviceRequestsRepo = serviceRequestsRepo;
	}

	@Transactional
	public UserDetails create(UserDetails details, String requestId) {
		// return the existing one
		if (serviceRequestsRepo.findByRequestId(requestId).isPresent()) {
			long id = serviceRequestsRepo.findByRequestId(requestId).get().getEntityId().getId();
			return userDetailsRepository.findById(id).get();

		} else {
			// save both
			LOGGER.info("saving user details");
			UserDetails savedUserDetails = userDetailsRepository.save(details);
			LOGGER.info("saving the service request id");
			serviceRequestsRepo
					.save(new ServiceRequests(requestId, savedUserDetails, EntityEnum.USER.getEntityTypeId()));
			return savedUserDetails;
		}

	}

	@Transactional
	public UserDetails create(UserDetails details) {
		return userDetailsRepository.save(details);
	}

	public List<UserDetails> getAll() {
		return userDetailsRepository.findAll();
	}

	public Page<UserDetails> getAllWithPagination(int pageNumber, int pageSize) {
		PageRequest pageReq = PageRequest.of(pageNumber, pageSize);
		return userDetailsRepository.findAll(pageReq);
	}

	public UserDetails getById(long id) {
		if (id <= 0) {
			throw new InvalidIdException("Please provide a valid id");
		}
		if (userDetailsRepository.findById(id).isPresent()) {
			return userDetailsRepository.findById(id).get();
		} else {
			throw new UserNotFoundException("user is not present with this id");
		}

	}

	@Transactional
	public UserDetails delete(long id) {
		if (id <= 0) {
			throw new InvalidIdException("Please provide a valid id");
		} else {
			if (userDetailsRepository.findById(id).isPresent()) {
				UserDetails deletedUserDetails = userDetailsRepository.findById(id).get();
				if (serviceRequestsRepo.findByEntityId(deletedUserDetails).isPresent()) {
					long deletedServiceRequestId = serviceRequestsRepo.findByEntityId(deletedUserDetails).get()
							.getId();
					LOGGER.info("Deleting the attached service request");
					serviceRequestsRepo.deleteById(deletedServiceRequestId);
				}

				LOGGER.info("Deleting the user information");
				userDetailsRepository.deleteById(deletedUserDetails.getId());
				return deletedUserDetails;
			} else {
				throw new UserNotFoundException("no user found with this id");
			}
		}
	}

	@Transactional
	public UserDetails update(UserDetails details) {
		long id = details.getId();
		if (id <= 0) {
			throw new InvalidIdException("Please provide a valid id");
		} else {
			if (userDetailsRepository.findById(id).isPresent()) {
				UserDetails dbUserDetails = userDetailsRepository.findById(id).get();

				// city
				if (details.getCity() != null) {
					dbUserDetails.setCity(details.getCity());
				}

				// country
				if (details.getCountry() != null) {
					dbUserDetails.setCountry(details.getCountry());
				}

				// email
				if (details.getEmail() != null) {
					dbUserDetails.setEmail(details.getEmail());
				}
				// username
				if (details.getUsername() != null) {
					dbUserDetails.setUsername(details.getUsername());
				}
				// name
				if (details.getName() != null) {
					dbUserDetails.setName(details.getName());
				}
				// phone no
				if (details.getPhoneNo() != null) {
					dbUserDetails.setPhoneNo(details.getPhoneNo());
				}
				// role
				if (details.getRole() != null) {
					dbUserDetails.setRole(details.getRole());
				}
				// state
				if (details.getState() != null) {
					dbUserDetails.setState(details.getState());
				}

				return userDetailsRepository.save(dbUserDetails);
			} else {
				throw new UserNotFoundException("no user found with this id");
			}
		}
	}
}
