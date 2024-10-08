package com.mysite.bank.useraccounts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountsRepository extends JpaRepository<UserAccounts, Long>{
	  List<UserAccounts> findByUser_UserName(String userName); 
	  UserAccounts findByAccountInfo_AccountId(Long accountId);
	  List<UserAccounts> findByUser_UserId(Long userId);
}
