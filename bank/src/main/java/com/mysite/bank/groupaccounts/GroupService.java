package com.mysite.bank.groupaccounts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.Event;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.groupaccountmembers.GroupAccountMembers;
import com.mysite.bank.groupaccountmembers.GroupAccountMembersRepository;
import com.mysite.bank.safelockers.SafeLockers;
import com.mysite.bank.safelockers.SafeLockersRepository;
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
    private final EventRepository eventRepository;
    private final SafeLockersRepository safeLockersRepository;
    
	 public GroupService(GroupAccountRepository groupAccountRepository, AccountInfoRepository accountInfoRepository, UsersRepository usersRepository, GroupAccountMembersRepository groupAccountMembersRepository, EventRepository eventRepository, SafeLockersRepository safeLockersRepository) {
	        this.groupAccountRepository = groupAccountRepository;
	        this.accountInfoRepository = accountInfoRepository;
	        this.usersRepository = usersRepository;
	        this.groupAccountMembersRepository = groupAccountMembersRepository;
	        this.eventRepository = eventRepository;
	        this.safeLockersRepository = safeLockersRepository;
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
    
   public void saveLocker(String lockerType, Long accountId) {
	   
	   AccountInfo accountInfo = accountInfoRepository.findById(accountId)
               .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	   
	   GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
			   .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
	   
	   groupAccount.setSafelockerType(lockerType);
	   groupAccountRepository.save(groupAccount);
	   
   }
   
   public void lockerStandard(Long transferThreshold, Long alertThreshold, Long accountId, String userName) {
	   AccountInfo accountInfo = accountInfoRepository.findById(accountId)
               .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	   
	   GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
			   .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
	   
	   groupAccount.setSafelockerThreshold(transferThreshold);
	   groupAccount.setAlertThreshold(alertThreshold);
	   
	   groupAccountRepository.save(groupAccount);
	   
	   Optional<Users> optionalUser = usersRepository.findByUserName(userName);
       if (!optionalUser.isPresent()) {
           throw new RuntimeException("User not found");
       }
       Users user = optionalUser.get();
	   
	   SafeLockers safeLockers = new SafeLockers();
	   safeLockers.setUser(user);
	   safeLockers.setGroupAccountId(groupAccount);
	   safeLockers.setCurrentBalanceWithInterest(0L);

	   if (groupAccount.getSafelockerType().equals("Flex")) {
		   safeLockers.setInterestRate(1.5);
	   } else if (groupAccount.getSafelockerType().equals("Premium")) {
		   safeLockers.setInterestRate(2.5);
	   } else {
		   safeLockers.setInterestRate(0.0);
	   }
	   safeLockersRepository.save(safeLockers);
	   
   }
   
   public String eventResult(String userName) {
	   
	   Optional<Users> optionalUser = usersRepository.findByUserName(userName);
       if (!optionalUser.isPresent()) {
           throw new RuntimeException("User not found");
       }
       Users user = optionalUser.get();
       Event event = eventRepository.findByUser_UserName(userName).get();
       String eventName = event.getEventName();
       return eventName;
   }
   
   // 모임통장에 필요한 모든 정보들 불러오기
	public Map<String, Object> groupAccountInfo(Long accountId) {
		
	    AccountInfo accountInfo = accountInfoRepository.findById(accountId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	    
	    GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
	    
	    Optional<SafeLockers> safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount);
//	    		.orElseThrow(() -> new IllegalArgumentException("Invalid safeLockers"));
	    
	    Map<String, Object> result = new HashMap<>();
	    
	    result.put("groupName", groupAccount.getGroupName());
	    result.put("groupBalance", groupAccount.getBalance());
	    result.put("safeLockerType", groupAccount.getSafelockerType());
	    result.put("safeLockerThreshold", groupAccount.getSafelockerThreshold());
	    result.put("alertThreshold", groupAccount.getAlertThreshold());
	    result.put("accountNum", accountInfo.getAccountNum());
	    
	    if (safeLockers.isEmpty()) { // safelocker가 [선택안함]인 경우
	    	result.put("currentBalance", 0);
	    } else { // safelocker가 flex, premium인 경우
	    result.put("currentBalance", safeLockers.get().getCurrentBalanceWithInterest());
	    }
	    
	    return result;
	}
	
	// safeLocker값이 locker로 넘어가는 로직
	public void updateBalance(Long groupBalance, Long currentBalance, Long accountId) {
		
		AccountInfo accountInfo = accountInfoRepository.findById(accountId)
		            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
		    
	    GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
		            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
		    
	    SafeLockers safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount)
	    		.orElseThrow(() -> new IllegalArgumentException("Invalid safeLockers"));
	    
	    groupAccount.setBalance(groupBalance);
	    safeLockers.setCurrentBalanceWithInterest(currentBalance);
	    
	    groupAccountRepository.save(groupAccount);
	    safeLockersRepository.save(safeLockers);
	}
}



