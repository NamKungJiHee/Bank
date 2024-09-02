package com.mysite.bank.transfer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
	List<Transfer> findByAccountInfo_AccountId(Long accountId);
}
