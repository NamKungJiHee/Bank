package com.mysite.bank.transfer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.EventRepository;
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
	
//public Transfer saveTransferInfo(String userName, Long accountId, String depositAccountNum, String depositBalance) {
//		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
//	       if (!optionalUser.isPresent()) {
//	           throw new RuntimeException("User not found");
//	       }
//	       Users user = optionalUser.get();
	       
//	       GroupAccount groupAccountInfo = groupAccountRepository.findByGroupAccountId(accountId);
//	       AccountInfo accountInfo = accountInfoRepository.findByAccountId(accountId);
	       
//	       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 현재 날짜, 시간
//	       Date date = new Date();
//	       String nowTime = dateFormat.format(date);

//	       String accountNum = accountInfo.getAccountNum();
	       
	       
//	}
}
