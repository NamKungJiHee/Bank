package com.mysite.bank.groupaccounts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;


@Service
public class GroupService {
    private final GroupAccountRepository groupAccountRepository;
    private final AccountInfoRepository accountInfoRepository;
    
	 public GroupService(GroupAccountRepository groupAccountRepository, AccountInfoRepository accountInfoRepository) {
	        this.groupAccountRepository = groupAccountRepository;
	        this.accountInfoRepository = accountInfoRepository;
	    }

	    @Transactional
	    public void save(String groupName, Long accountId) {
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
	    }
}
