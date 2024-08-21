package com.mysite.bank.groupaccountmembers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.bank.groupaccounts.GroupAccount;


public interface GroupAccountMembersRepository extends JpaRepository<GroupAccountMembers, Long>{
	List<GroupAccountMembers> findByUser_UserName(String userName); 	
}



