package com.mysite.bank.groupaccounts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
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
import com.mysite.bank.useraccounts.UserAccounts;
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
    private final UserAccountsRepository userAccountRepository;
    
	 public GroupService(GroupAccountRepository groupAccountRepository, AccountInfoRepository accountInfoRepository, UsersRepository usersRepository, GroupAccountMembersRepository groupAccountMembersRepository, EventRepository eventRepository, SafeLockersRepository safeLockersRepository, UserAccountsRepository userAccountRepository) {
	        this.groupAccountRepository = groupAccountRepository;
	        this.accountInfoRepository = accountInfoRepository;
	        this.usersRepository = usersRepository;
	        this.groupAccountMembersRepository = groupAccountMembersRepository;
	        this.eventRepository = eventRepository;
	        this.safeLockersRepository = safeLockersRepository;
	        this.userAccountRepository = userAccountRepository;
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
		   safeLockers.setInterestRate(1.3);
	   } else if (groupAccount.getSafelockerType().equals("Premium")) {
		   safeLockers.setInterestRate(2.3);
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
   
   // 모임통장에 필요한 모든 정보들 불러오기 (단)
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
	
	// 이자율 계산 로직
	@Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
	//@Scheduled(cron = "0 */1 * * * *") // TEST
	public void interestRate() {
	
	    List<SafeLockers> allLockers = safeLockersRepository.findAll();
	    Long interest;
	    
	    for (SafeLockers locker : allLockers) {
	        Double rateType = locker.getInterestRate();
	        
	        // GroupAccount groupAccount = locker.getGroupAccountId();
	        // Long groupBalance = groupAccount.getBalance(); 
	        Long interestBalance = locker.getCurrentBalanceWithInterest();
	        System.out.println("INTERESTBALANCE: " + interestBalance);
	        
	        String userId = locker.getUser().getUserName();
	        
	        // 이벤트에 따라 혜택 다르게
	        String eventName = this.eventRepository.findByUser_UserName(userId).get().getEventName();
	        
	        // 이자 계산 및 반영
	        if (rateType.equals(1.3)) {
	        	  if ("추가이자율 1%지급".equals(eventName)) {
		            	interest = Math.round(interestBalance * 0.023); // 2.3% 이자 계산
			            locker.setCurrentBalanceWithInterest(interestBalance + interest); 
			            safeLockersRepository.save(locker);
		            } else {
		            	interest = Math.round(interestBalance * 0.013); // 1.3% 이자 계산
		 	            locker.setCurrentBalanceWithInterest(interestBalance + interest); 
		 	            safeLockersRepository.save(locker);
		            }
	        } else if (rateType.equals(2.3)) {
	        	if ("추가이자율 1%지급".equals(eventName)) {
	        		interest = Math.round(interestBalance * 0.033); // 3.3% 이자 계산
		        	locker.setCurrentBalanceWithInterest(interestBalance + interest); 
		        	safeLockersRepository.save(locker);
	        	} else {
		        	interest = Math.round(interestBalance * 0.023); // 2.3% 이자 계산
		        	locker.setCurrentBalanceWithInterest(interestBalance + interest); 
		        	safeLockersRepository.save(locker);
	        	}
	        }
	    }
	}
	
	public String userNickName(String userName) {
		 Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	       if (!optionalUser.isPresent()) {
	           throw new RuntimeException("User not found");
	       }
	       
	       Users user = optionalUser.get();
	       String nickName = user.getUserNickname();
	       
	       return nickName;
	}
	
	public Long userId(String userName) {
		 Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	       if (!optionalUser.isPresent()) {
	           throw new RuntimeException("User not found");
	       }
	       
	       Users user = optionalUser.get();
	       Long userId = user.getUserId();
	       
	       return userId;
	}
	
	// 모임통장에 필요한 모든 정보들 불러오기 (복)
    public List<Map<String, Object>> getGroupAccountInfosByUserId(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userName = user.getUserName();

        List<GroupAccountMembers> members = groupAccountMembersRepository.findByUser_UserName(userName);

        List<GroupAccount> groupAccounts = members.stream()
            .map(GroupAccountMembers::getGroupAccountId)
            .distinct()
            .collect(Collectors.toList());

        return groupAccounts.stream()
            .map(groupAccount -> {
                Map<String, Object> result = new HashMap<>();
                result.put("groupName", groupAccount.getGroupName());
                result.put("groupBalance", groupAccount.getBalance());
                result.put("safeLockerType", groupAccount.getSafelockerType());
                result.put("safeLockerThreshold", groupAccount.getSafelockerThreshold());
                result.put("alertThreshold", groupAccount.getAlertThreshold());
                result.put("accountNum", groupAccount.getAccountInfo().getAccountNum());
                result.put("accountId", groupAccount.getGroupAccountId());
                
                SafeLockers safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount)
                        .orElse(new SafeLockers()); 

                Long currentBalance = safeLockers.getCurrentBalanceWithInterest();

                result.put("currentBalance", currentBalance != null ? currentBalance : 0L);
                return result;
            })
            .collect(Collectors.toList());
    }
    
    public GroupAccount getGroupAccountInfoByGroupAccountId(Long groupAccountId) {
        return groupAccountRepository.findByGroupAccountId(groupAccountId);
    }
    
    public Long getCurrentBalance(GroupAccount groupAccount) { 
    	SafeLockers safeLockers = safeLockersRepository.findByGroupAccountId(groupAccount)
                .orElse(new SafeLockers()); 
    	 Long currentBalance = safeLockers.getCurrentBalanceWithInterest();
    	 
    	 if (currentBalance == null) {
    		 currentBalance = 0L;
    	 }
    	 return currentBalance;
    }
    
    // 기존 모임통장 멤버 가져오기
    public List<String> getOriginMember(Long accountId) {
    	UserAccounts accountInfo = userAccountRepository.findByAccountInfo_AccountId(accountId);
    	Long userId = accountInfo.getUser().getUserId();

    	Optional<Users> userInfo = usersRepository.findByUserId(userId);
    	String userNickName = userInfo.get().getUserNickname();

    	List<String> nickName = new ArrayList<>();
    	nickName.add(userNickName);
    	return nickName;
    }
    
}



