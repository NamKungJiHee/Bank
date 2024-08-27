package com.mysite.bank.groupaccounts;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.useraccounts.UserAccounts;
import com.mysite.bank.users.Users;


public interface GroupAccountRepository extends JpaRepository<GroupAccount, Long>{
	Optional<GroupAccount> findByAccountInfo(AccountInfo accountInfo);
	GroupAccount findByGroupAccountId(Long groupAccountId);
	GroupAccount findByAccountInfo_AccountId(Long accountId);
}