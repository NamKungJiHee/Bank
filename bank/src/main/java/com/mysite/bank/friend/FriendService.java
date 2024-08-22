package com.mysite.bank.friend;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.bank.event.EventRepository;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FriendService {
	private final FriendRepository friendRepository;
	private final UsersRepository usersRepository;
	
	public Friend save(String userName) {
	
		Optional<Users> optionalUser = usersRepository.findByUserName(userName);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Users user = optionalUser.get();
        String defaultStatus = "PENDING";
        
        Friend friend = new Friend();
        friend.setInviterUserId(user);
        friend.setInvitedUserId(null);
        friend.setStatus(defaultStatus);
        friendRepository.save(friend);
        
        return friend;
	}
	
}
