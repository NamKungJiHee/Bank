package com.mysite.bank.friend;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.users.Users;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	Friend findByGroupAccountId(GroupAccount groupAccountId);
	Optional<Friend> findByInvitedUserId(Users invitedUserId);
	//Optional<Friend> findByInvitedUserId(Long invitedUserId);
}
