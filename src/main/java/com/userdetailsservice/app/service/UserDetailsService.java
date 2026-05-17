package com.userdetailsservice.app.service;

import java.util.List;

import javax.management.relation.InvalidRelationIdException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.userdetailsservice.app.entityenum.EntityEnum;
import com.userdetailsservice.app.exp.InvalidIdException;
import com.userdetailsservice.app.exp.MissingOrInvalidSyncTokenException;
import com.userdetailsservice.app.exp.StaleObjectError;
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
	private RedisService redisService;

	UserDetailsService(UserDetailsRepository userDetailsRepository, ServiceRequestsRepo serviceRequestsRepo,
			RedisService redisService) {
		this.userDetailsRepository = userDetailsRepository;
		this.serviceRequestsRepo = serviceRequestsRepo;
		this.redisService = redisService;
	}

	@Transactional
	public UserDetails create(UserDetails details, String requestId) {
		// return the existing one
		if (serviceRequestsRepo.findByRequestId(requestId).isPresent()) {
			long id = serviceRequestsRepo.findByRequestId(requestId).get().getEntityId().getId();

			// using redis to get the cached data
			if (userDetailsRepository.findById(id).isPresent()) {
				// adding redis
				UserDetails userDetails = redisService.get("user_id_" + id, UserDetails.class);

				if (userDetails != null) {
					LOGGER.info("getting from redis...");
					return userDetails;
				} else {
					UserDetails dbUserDetails = userDetailsRepository.findById(id).get();
					// save into the redis
					redisService.set("user_id_" + id, dbUserDetails, 300L);
					return dbUserDetails;
				}
			}else {
				//should not come here (Deletion time removing both reqId and user)
				throw new UserNotFoundException("Something unexpected happended : please try with a different request id");
				
			}

		} else {
			// save both
			LOGGER.info("saving user details");
			// synctoken will start with 0
			details.setSyncToken("0");
			UserDetails savedUserDetails = userDetailsRepository.save(details);
			LOGGER.info("saving the service request id");
			serviceRequestsRepo
					.save(new ServiceRequests(requestId, savedUserDetails, EntityEnum.USER.getEntityTypeId()));
			return savedUserDetails;
		}

	}

	@Transactional
	public UserDetails create(UserDetails details) {
		// synctoken will start with 0
		details.setSyncToken("0");
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
			// adding redis
			UserDetails userDetails = redisService.get("user_id_" + id, UserDetails.class);

			if (userDetails != null) {
				LOGGER.info("getting from redis...");
				return userDetails;
			} else {
				UserDetails dbUserDetails = userDetailsRepository.findById(id).get();
				// save into the redis
				redisService.set("user_id_" + id, dbUserDetails, 300L);
				return dbUserDetails;
			}
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
					long deletedServiceRequestId = serviceRequestsRepo.findByEntityId(deletedUserDetails).get().getId();
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
			if (details.getSyncToken() == null) {
				throw new MissingOrInvalidSyncTokenException(
						"Required field is missing : please provide the SyncToken value");
			}
			if (userDetailsRepository.findById(id).isPresent()) {
				UserDetails dbUserDetails = userDetailsRepository.findById(id).get();
				String currentSynctoken = dbUserDetails.getSyncToken();
				if (Integer.parseInt(details.getSyncToken()) < Integer.parseInt(currentSynctoken)) {
					throw new StaleObjectError("Stale Object Error : Please use the latest sync token");
				} else {

					if (Integer.parseInt(details.getSyncToken()) != Integer.parseInt(currentSynctoken)) {
						throw new MissingOrInvalidSyncTokenException("Please provide the valid synctoken");
					}
					Boolean changeDetected = false;
					// city
					if (details.getCity() != null) {
						if (!(details.getCity().equals(dbUserDetails.getCity()))) {
							changeDetected = true;
							dbUserDetails.setCity(details.getCity());
						}

					}

					// country
					if (details.getCountry() != null) {
						if (!(details.getCountry().equals(dbUserDetails.getCountry()))) {
							changeDetected = true;
							dbUserDetails.setCountry(details.getCountry());
						}

					}

					// email
					if (details.getEmail() != null) {

						if (!(details.getEmail().equals(dbUserDetails.getEmail()))) {
							changeDetected = true;
							dbUserDetails.setEmail(details.getEmail());
						}

					}
					// username
					if (details.getUsername() != null) {

						if (!(details.getUsername().equals(dbUserDetails.getUsername()))) {
							changeDetected = true;
							dbUserDetails.setUsername(details.getUsername());
						}

					}
					// name
					if (details.getName() != null) {

						if (!(details.getName().equals(dbUserDetails.getName()))) {
							changeDetected = true;
							dbUserDetails.setName(details.getName());
						}

					}
					// phone no
					if (details.getPhoneNo() != null) {

						if (!(details.getPhoneNo().equals(dbUserDetails.getPhoneNo()))) {
							changeDetected = true;
							dbUserDetails.setPhoneNo(details.getPhoneNo());
						}

					}

					// role
					if (details.getRole() != null) {

						if (!(details.getRole().equals(dbUserDetails.getRole()))) {
							changeDetected = true;
							dbUserDetails.setRole(details.getRole());
						}

					}
					// state
					if (details.getState() != null) {
						if (!(details.getState().equals(dbUserDetails.getState()))) {
							changeDetected = true;
							dbUserDetails.setState(details.getState());
						}

					}

					if (changeDetected) {
						dbUserDetails.setSyncToken(String.valueOf(Long.parseLong(currentSynctoken) + 1));
					}

				}

				return userDetailsRepository.save(dbUserDetails);
			} else {
				throw new UserNotFoundException("no user found with this id");
			}
		}
	}
}
