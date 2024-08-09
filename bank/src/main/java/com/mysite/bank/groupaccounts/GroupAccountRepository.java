package com.mysite.bank.groupaccounts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.users.Users;


public interface GroupAccountRepository extends JpaRepository<GroupAccount, Long>{
	Optional<GroupAccount> findByAccountInfo(AccountInfo accountInfo);
	
}