package com.mysite.bank.transfer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.friend.Friend;
import com.mysite.bank.friend.FriendRepository;
import com.mysite.bank.groupaccountmembers.GroupAccountMembers;
import com.mysite.bank.groupaccountmembers.GroupAccountMembersRepository;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.groupaccounts.GroupAccountRepository;
import com.mysite.bank.safelockers.SafeLockersRepository;
import com.mysite.bank.useraccounts.UserAccounts;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransferService {
	private final TransferRepository transferRepository;
	private final UsersRepository usersRepository;
	private final GroupAccountRepository groupAccountRepository;
	private final UserAccountsRepository userAccountsRepository;
	private final AccountInfoRepository accountInfoRepository;
	private final GroupAccountMembersRepository groupAccountMembersRepository;
	private final FriendRepository friendRepository;
	
	public List<Map<String, Object>> groupAccountInfo(String userName) {
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	       if (!optionalUser.isPresent()) {
	           throw new RuntimeException("User not found");
	       }
	      
	       Long userId = optionalUser.get().getUserId();
	      
	       List<UserAccounts> accountInfoList = userAccountsRepository.findByUser_UserId(userId);
	       
	       if (accountInfoList.isEmpty()) {
	           return Collections.emptyList();
	       }
	       
	       List<Map<String, Object>> resultList = new ArrayList<>();

	       for (UserAccounts accountInfo : accountInfoList) {
	           Long accountId = accountInfo.getAccountInfo().getAccountId();

	           GroupAccount groupAccountInfo = groupAccountRepository.findByAccountInfo_AccountId(accountId);
	           if (groupAccountInfo == null) {
	        	   System.out.println("GroupAccount not found for accountId: " + accountId);
	           }
	           
	           AccountInfo accountInformation = accountInfoRepository.findByAccountId(accountId);
	           if (accountInformation == null) {
	               throw new RuntimeException("AccountInfo not found for accountId: " + accountId);
	           }

	           Long isGroupAccount = accountInformation.getIsGroupaccount();
	           Long balance;
	           if (isGroupAccount == 0) {
	        	   balance = accountInformation.getBalance();
	           } else {
	        	   balance = groupAccountInfo.getBalance();
   
	           }	           
	           
  	           String accountNum = accountInformation.getAccountNum();
	          // Long getAccountId = groupAccountInfo.getAccountInfo().getAccountId();
	           
	           Map<String, Object> result = new HashMap<>();
	           result.put("balance", balance);
	           result.put("accountNum", accountNum);
//	           result.put("accountId", getAccountId);
	           result.put("accountId", accountId);

	           resultList.add(result);
	       }

	       return resultList;
	   }
	
	// 직접입력 이체
	public Transfer saveTransferInfo(String userName, Long accountId, String depositAccountNum, String depositBalance) {
		Long balance;	
		Long updateBalance;
		String transactionType;
		
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	    if (!optionalUser.isPresent()) {
	        throw new RuntimeException("User not found");
	    }
	    Users user = optionalUser.get();
	    
	    // 현재 날짜 및 시간
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 a HH시 mm분");
	    LocalDateTime nowLocalDateTime = LocalDateTime.now();
	  
	    Long addBalance = Long.parseLong(depositBalance); // amount(거래금액)
	    
	    Transfer transfer = new Transfer();
	    
		// 출금계좌 확인(일반통장인지, 모임통장인지)
	    GroupAccount groupAccountInfoInput = groupAccountRepository.findByAccountInfo_AccountId(accountId); // 모임통장 엔티티
	    AccountInfo accountInfo = accountInfoRepository.findByAccountId(accountId); // 일반통장 엔티티
		AccountInfo selectAccount = accountInfoRepository.findByAccountId(accountId);
		Long selectAccountIsGroupAccount = selectAccount.getIsGroupaccount(); // 0,1
		transactionType = "출금";
		
		if (selectAccountIsGroupAccount == 1) { // 모임통장일 경우
			transfer.setUser(user);
			transfer.setGroupAccountId(groupAccountInfoInput);
			transfer.setAccountInfo(accountInfo);
			transfer.setAmount(addBalance);
			transfer.setTransactionType(transactionType);
			transfer.setTransactionTime(nowLocalDateTime);
			
			balance = groupAccountInfoInput.getBalance();  
			updateBalance = balance - addBalance;
			transfer.setLeftBalance(updateBalance); // 잔액
			groupAccountInfoInput.setBalance(updateBalance);
			groupAccountRepository.save(groupAccountInfoInput);
			transferRepository.save(transfer); 
		} else {
			transfer.setUser(user);
			transfer.setGroupAccountId(null);
			transfer.setAccountInfo(accountInfo);
			transfer.setAmount(addBalance);
			transfer.setTransactionType(transactionType);
			transfer.setTransactionTime(nowLocalDateTime);
			
			balance = accountInfo.getBalance();
			updateBalance = balance - addBalance;
			transfer.setLeftBalance(updateBalance); // 잔액
			accountInfo.setBalance(updateBalance);
			accountInfoRepository.save(accountInfo);
			transferRepository.save(transfer); 
		}
		
		// 입금계좌 확인(일반통장인지, 모임통장인지)  
		// 존재하는 계좌번호인지 확인, 입금계좌가 모임통장인지 일반통장인지 확인
	   AccountInfo accountNum = accountInfoRepository.findByAccountNum(depositAccountNum); // 계좌 엔티티
	   AccountInfo originAccountInfo = accountInfoRepository.findByAccountNum(depositAccountNum); // 일반 통장 엔티티
	   GroupAccount groupAccountInfo = null;
	
	   if (accountNum.getIsGroupaccount() == 1) {
		   groupAccountInfo = groupAccountRepository.findByAccountInfo_AccountId(accountNum.getAccountId()); // 모임통장 엔티티
	   } else {
		   groupAccountInfo = null;
	   }
	   
	   if (accountNum != null) {
		   if (accountNum.getIsGroupaccount() == 1) { // 모임통장에 입금하는 경우
			 balance = groupAccountInfo.getBalance();  
			 updateBalance = balance + addBalance;
			 transactionType = "입금";
			 
			 Transfer depositTransfer = new Transfer();
			 depositTransfer.setUser(user);
			 depositTransfer.setGroupAccountId(groupAccountInfo);
			 depositTransfer.setAccountInfo(originAccountInfo);
			 depositTransfer.setAmount(addBalance);
			 depositTransfer.setTransactionType(transactionType);
			 depositTransfer.setTransactionTime(nowLocalDateTime);
			 depositTransfer.setLeftBalance(updateBalance); // 잔액
			 groupAccountInfo.setBalance(updateBalance);
			 groupAccountRepository.save(groupAccountInfo);
			 transferRepository.save(depositTransfer);
		   } else { // 일반통장에 입금하는 경우
			   balance = accountNum.getBalance();
			   updateBalance = balance + addBalance;
			   transactionType = "입금";
			   
			   Transfer depositTransfer = new Transfer();
			   depositTransfer.setUser(user);
			   depositTransfer.setGroupAccountId(groupAccountInfo);
			   depositTransfer.setAccountInfo(originAccountInfo);
			   depositTransfer.setAmount(addBalance);
			   depositTransfer.setTransactionType(transactionType);
			   depositTransfer.setTransactionTime(nowLocalDateTime);
			   depositTransfer.setLeftBalance(updateBalance); // 잔액
			   accountNum.setBalance(updateBalance);
			   accountInfoRepository.save(accountNum);
			   transferRepository.save(depositTransfer);
		   }
	   } else {
		   throw new RuntimeException("존재하지 않는 계좌번호입니다.");
	   }
		   return transfer;    
	}
	
	public List<Map<String, Object>> groupAccountList(String userName, Long selectAccountId) {
	
	    List<GroupAccountMembers> groupMembers = groupAccountMembersRepository.findByUser_UserName(userName);
	    
	    Long userId = usersRepository.findByUserName(userName).get().getUserId(); // 사용자Id
	    List<Friend> invitedFriends = friendRepository.findByInvitedUserId_UserId(userId);

	    List<Map<String, Object>> groupDetails = new ArrayList<>();
	    
	    List<GroupAccount> groupAccountIds = invitedFriends.stream()
        .filter(friend -> friend.getInvitedUserId().getUserId().equals(userId) && "ACCEPTED".equals(friend.getStatus())) // invited_user_id에 해당하고, status가 accepted인 경우의 group_account_id만 추출
        .map(Friend::getGroupAccountId)
        .collect(Collectors.toList());

	    List<Map<String, Object>> invitedGroupDetails = groupAccountIds.stream()
	    	.map(groupInfo -> {
	    		GroupAccount groupAccount = groupAccountRepository.findByAccountInfo_AccountId(groupInfo.getAccountInfo().getAccountId());
	    		Long accountId = groupAccount.getAccountInfo().getAccountId(); // 계좌번호
                Map<String, Object> details = new HashMap<>();

                String groupName = groupAccount.getGroupName();
                String accountNum = groupAccount.getAccountInfo().getAccountNum();
                
                details.put("groupName", groupName);
                details.put("accountNum", accountNum);
                details.put("accountId", accountId);
            	return details;
	    		
	    	}).collect(Collectors.toList());
	    groupDetails.addAll(invitedGroupDetails);
	    
	    List<Map<String, Object>> groupAccountGroupDetails = groupMembers.stream()
	            .map(groupMember -> {
	                GroupAccount groupAccount = groupMember.getGroupAccountId();
	                Long accountId = groupAccount.getAccountInfo().getAccountId(); // 계좌번호
	                Map<String, Object> details = new HashMap<>();

	                if (selectAccountId.longValue() != accountId.longValue()) { // 입출금 통장이 같으면 안뜨도록
	                	String groupName = groupAccount.getGroupName();
	 	                String accountNum = groupAccount.getAccountInfo().getAccountNum();
	 	                details.put("groupName", groupName);
	 	                details.put("accountNum", accountNum);
	 	                details.put("accountId", accountId);
	                }
 	                return details; 
	            })
	            .collect(Collectors.toList());
	    
	    groupDetails.addAll(groupAccountGroupDetails);
	    groupDetails.removeIf(Map::isEmpty);
	  
	    return groupDetails;
	}
	
	// 모임통장 이체
	public Transfer saveGroupTransferInfo(String userName, Long selectAccountId, String depositBalance, Long accountId) {
		Long balance;	
		Long updateBalance;
		String transactionType;
		
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	    if (!optionalUser.isPresent()) {
	        throw new RuntimeException("User not found");
	    }
	    Users user = optionalUser.get();
	    
	    // 현재 날짜 및 시간
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 a HH시 mm분");
	    LocalDateTime nowLocalDateTime = LocalDateTime.now();
	    
	    Long addBalance = Long.parseLong(depositBalance); // amount(거래금액)
	    
	    Transfer transfer = new Transfer();
	 // 출금계좌 확인(일반통장인지, 모임통장인지)
	    GroupAccount groupAccountInfoInput = groupAccountRepository.findByAccountInfo_AccountId(accountId); // 모임통장 엔티티
	    AccountInfo accountInfo = accountInfoRepository.findByAccountId(accountId); // 일반통장 엔티티
		AccountInfo selectAccount = accountInfoRepository.findByAccountId(accountId);
		Long selectAccountIsGroupAccount = selectAccount.getIsGroupaccount(); // 0,1
		transactionType = "출금";
		
		if (selectAccountIsGroupAccount == 1) { // 모임통장일 경우
			transfer.setUser(user);
			transfer.setGroupAccountId(groupAccountInfoInput);
			transfer.setAccountInfo(accountInfo);
			transfer.setAmount(addBalance);
			transfer.setTransactionType(transactionType);
			transfer.setTransactionTime(nowLocalDateTime);
			
			balance = groupAccountInfoInput.getBalance();  
			updateBalance = balance - addBalance;
			transfer.setLeftBalance(updateBalance); // 잔액
			groupAccountInfoInput.setBalance(updateBalance);
			groupAccountRepository.save(groupAccountInfoInput);
			transferRepository.save(transfer); 
		} else {
			transfer.setUser(user);
			transfer.setGroupAccountId(null);
			transfer.setAccountInfo(accountInfo);
			transfer.setAmount(addBalance);
			transfer.setTransactionType(transactionType);
			transfer.setTransactionTime(nowLocalDateTime);
			
			balance = accountInfo.getBalance();
			updateBalance = balance - addBalance;
			transfer.setLeftBalance(updateBalance); // 잔액
			accountInfo.setBalance(updateBalance);
			accountInfoRepository.save(accountInfo);
			transferRepository.save(transfer); 
		}
		
		// 입금계좌 확인(일반통장인지, 모임통장인지)  
		// 입금계좌가 모임통장인지 일반통장인지 확인
	   Long isGroupAccount = accountInfoRepository.findByAccountId(selectAccountId).getIsGroupaccount(); // 선택된 계좌의 모임통장 여부
	   
	   AccountInfo originAccountInfo = accountInfoRepository.findByAccountId(selectAccountId); // 일반 통장 엔티티
	   GroupAccount groupAccountInfo = null;

	   if (isGroupAccount == 1) {
		   groupAccountInfo = groupAccountRepository.findByAccountInfo_AccountId(selectAccountId); // 모임통장 엔티티
	   } else {
		   groupAccountInfo = null;
	   }
	   
	   // 모임통장에 입금하는 경우
	   if (isGroupAccount==1) {
		   	balance = groupAccountInfo.getBalance();  
		   	updateBalance = balance + addBalance;
		   	transactionType = "입금";
			 
		   	Transfer depositTransfer = new Transfer();
		   	depositTransfer.setUser(user);
		   	depositTransfer.setGroupAccountId(groupAccountInfo);
	   		depositTransfer.setAccountInfo(originAccountInfo);
	   		depositTransfer.setAmount(addBalance);
   			depositTransfer.setTransactionType(transactionType);
			depositTransfer.setTransactionTime(nowLocalDateTime);
			depositTransfer.setLeftBalance(updateBalance); // 잔액
			groupAccountInfo.setBalance(updateBalance);
			groupAccountRepository.save(groupAccountInfo);
			transferRepository.save(depositTransfer);
   		} else { // 일반통장에 입금하는 경우
			   balance = originAccountInfo.getBalance();
			   updateBalance = balance + addBalance;
			   transactionType = "입금";
			   
			   Transfer depositTransfer = new Transfer();
			   depositTransfer.setUser(user);
			   depositTransfer.setGroupAccountId(groupAccountInfo);
			   depositTransfer.setAccountInfo(originAccountInfo);
			   depositTransfer.setAmount(addBalance);
			   depositTransfer.setTransactionType(transactionType);
			   depositTransfer.setTransactionTime(nowLocalDateTime);
			   depositTransfer.setLeftBalance(updateBalance); // 잔액
			   originAccountInfo.setBalance(updateBalance);
			   accountInfoRepository.save(originAccountInfo);
			   transferRepository.save(depositTransfer);
		   }
		   return transfer;    
	}
	
	// 거래내역 가져오기
	public List<Map<String, Object>> transactionHistory(Long accountId) {
		List<Transfer> transferList = transferRepository.findByAccountInfo_AccountId(accountId);
		
		if (transferList.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		for(Transfer transfer: transferList) {
			Long userId = transfer.getUser().getUserId();
			String userName = usersRepository.getById(userId).getUserNickname();
			Long balance = transfer.getLeftBalance();
			
			Map<String, Object> result = new HashMap<>();
			result.put("transactionTime", transfer.getTransactionTime());
			result.put("name", userName);
			result.put("transactionType", transfer.getTransactionType());
			result.put("transactionAmount", transfer.getAmount());
			result.put("balance", balance);
			resultList.add(result);
		}
		 return resultList;
	}

}
