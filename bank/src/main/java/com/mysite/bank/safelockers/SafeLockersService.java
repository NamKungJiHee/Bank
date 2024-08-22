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
    private final EventRepository eventRepository;
    
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
    
    // [내모임통장으로출금] 부분
    public Long sendBalance(Long withdrawBalance, Long accountId, String userName) {
    	
    	Long afterGroupBalance;
    	
    	AccountInfo accountInfo = accountInfoRepository.findById(accountId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	    
    	GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
	    
    	SafeLockers safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount)
    		.orElseThrow(() -> new IllegalArgumentException("Invalid safeLockers"));
    	
    	String eventName = eventRepository.findByUser_UserName(userName).get().getEventName();
    	
    	Long currentBalance = safeLockers.getCurrentBalanceWithInterest(); // 현재 safelocker에 담겨져있는 금액
    	Long currentGroupBalance = groupAccount.getBalance(); // 현재 모임통장에 담겨져있는 금액
    	
    	Long afterBalanceInterest = currentBalance - withdrawBalance; // 출금 후 safeLocker 금액
    	
    	if ("SafeLocker 첫출금시 십만원 추가지급".equals(eventName)) {
    		afterGroupBalance = currentGroupBalance + withdrawBalance + 100000; // 출금 후 모임통장 금액
    	} else {
    		afterGroupBalance = currentGroupBalance + withdrawBalance; // 출금 후 모임통장 금액
    	}
    	
    	groupAccount.setBalance(afterGroupBalance);
    	safeLockers.setCurrentBalanceWithInterest(afterBalanceInterest);
    	
    	groupAccountRepository.save(groupAccount);
    	safeLockersRepository.save(safeLockers);
    	
    	return afterBalanceInterest;
    }
}
