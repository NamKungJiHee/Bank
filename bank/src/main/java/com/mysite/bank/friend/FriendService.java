package com.mysite.bank.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.groupaccounts.GroupAccountRepository;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FriendService {
	private final FriendRepository friendRepository;
	private final UsersRepository usersRepository;
	private final AccountInfoRepository accountInfoRepository;
	private final GroupAccountRepository groupAccountRepository;
	
	public Friend save(String userName, Long accountId) {
	
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Users user = optionalUser.get();
        String defaultStatus = "PENDING";
        
        AccountInfo accountInfo = accountInfoRepository.findById(accountId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	    
	    GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
        
        Friend friend = new Friend();
        friend.setInviterUserId(user);
        friend.setInvitedUserId(null);
        friend.setStatus(defaultStatus);
        friend.setGroupAccountId(groupAccount);
        friendRepository.save(friend);
        
        return friend;
	}
	
	// update inviteStatus
	public Friend saveInvitedFriend(String userName, Long accountId) {
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Users user = optionalUser.get();
        String updateStatus = "ACCEPTED";
        
        AccountInfo accountInfo = accountInfoRepository.findById(accountId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
	    
	    GroupAccount groupAccount = groupAccountRepository.findByAccountInfo(accountInfo)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid groupAccount"));
        
        Friend friend = friendRepository.findByGroupAccountId(groupAccount);
        friend.setStatus(updateStatus);
        friend.setInvitedUserId(user);
        friendRepository.save(friend);
        
        return friend;
	}

	public List<Friend> findInvitedId(Long userId) {
		Optional<Users> userInfo = usersRepository.findByUserId(userId);
		Users user = userInfo.get();
		//List<Friend> invitedUserId = friendRepository.findByInvitedUserId(user);
		
		return friendRepository.findByInvitedUserId(user);
	}


}
