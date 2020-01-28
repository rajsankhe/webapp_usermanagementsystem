package com.usermanagement.repositories;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanagement.models.Bill;
import com.usermanagement.models.User;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID>{
		
	List<Bill> findByOwnerId(UUID ownerid);

	Bill findByOwnerIdAndBillId(UUID ownerId, UUID billID);
}
