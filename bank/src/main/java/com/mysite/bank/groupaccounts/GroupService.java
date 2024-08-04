package com.mysite.bank.groupaccounts;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.groupaccountmembers.GroupAccountMembers;
import com.mysite.bank.groupaccountmembers.GroupAccountMembersRepository;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;


import lombok.RequiredArgsConstructor;


@Service
public class GroupService {
    private final GroupAccountRepository groupAccountRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final UsersRepository usersRepository;
    private final GroupAccountMembersRepository groupAccountMembersRepository;
    
	 public GroupService(GroupAccountRepository groupAccountRepository, AccountInfoRepository accountInfoRepository, UsersRepository usersRepository, GroupAccountMembersRepository groupAccountMembersRepository) {
	        this.groupAccountRepository = groupAccountRepository;
	        this.accountInfoRepository = accountInfoRepository;
	        this.usersRepository = usersRepository;
	        this.groupAccountMembersRepository = groupAccountMembersRepository;
	    }

	    @Transactional
	    public void save(String groupName, Long accountId, String userName) {
	        
	    	Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	        if (!optionalUser.isPresent()) {
	            throw new RuntimeException("User not found");
	        }
	        Users user = optionalUser.get();
	    	
	    	AccountInfo accountInfo = accountInfoRepository.findById(accountId)
	                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	        GroupAccount groupAccount = new GroupAccount();
	        groupAccount.setGroupName(groupName);
	        groupAccount.setAccountInfo(accountInfo);
	        groupAccount.setBalance(0L);
	        groupAccount.setSafelockerType("default");
	        groupAccount.setSafelockerThreshold(0L);
	        groupAccount.setAlertThreshold(0L);
	        groupAccountRepository.save(groupAccount);
	        
	        GroupAccountMembers groupAccountMembers = new GroupAccountMembers();
	        groupAccountMembers.setUser(user);
	        groupAccountMembers.setGroupAccountId(groupAccount);
	        groupAccountMembersRepository.save(groupAccountMembers);
	    }
}
