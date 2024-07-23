package com.mysite.bank.accountinfo;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.bank.users.ApplicationEnvironmentConfig;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountInfoService {
	private final AccountInfoRepository accountInfoRepository;
	
	@Transactional
	public AccountInfo create(Long accountPassword) {
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setAccountPassword(accountPassword);
	    accountInfo.setAccountNum(generateUniqueAccountNum());
	    accountInfo.setBalance(0L); 
	    accountInfo.setIsGroupaccount(0L); 
		this.accountInfoRepository.save(accountInfo);
		return accountInfo;
	}
	
	private String generateUniqueAccountNum() {

		StringBuilder sb = new StringBuilder();
		Random rd = new Random();
		
		for(int i=0; i<11; i++) {
			sb.append(rd.nextInt(10));
		}
		
		String accountNum = sb.toString();
		if (sb.length() == 11) {
			sb.toString();
			StringBuffer buf = new StringBuffer(sb);
			buf.insert(3, "-");
			buf.insert(7, "-");
			accountNum = buf.toString();
		}

		return accountNum;
	}
	
	
}

