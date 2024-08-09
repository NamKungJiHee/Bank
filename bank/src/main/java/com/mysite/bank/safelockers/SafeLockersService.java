package com.mysite.bank.safelockers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.groupaccountmembers.GroupAccountMembersRepository;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.groupaccounts.GroupAccountRepository;
import com.mysite.bank.users.ApplicationEnvironmentConfig;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class SafeLockersService {
    private final GroupAccountRepository groupAccountRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final SafeLockersRepository safeLockersRepository;
    
    public Map<String, Object> getLockerType(Long accountId) {
    	 AccountInfo accountInfo = accountInfoRepository.findById(accountId)
 	            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
 	    
 	    GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
 	            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
 	    
 	   Optional<SafeLockers> safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount);
 	  
 	   Map<String, Object> result = new HashMap<>();
 	   
 	   result.put("lockerType", groupAccount.getSafelockerType());
 	   result.put("currentBalance", safeLockers.get().getCurrentBalanceWithInterest());
 	   
 	   return result;
    }
}
