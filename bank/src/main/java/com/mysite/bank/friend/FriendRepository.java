package com.mysite.bank.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.users.Users;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	Friend findByGroupAccountId(GroupAccount groupAccountId);
	List<Friend> findByInvitedUserId(Users invitedUserId);
	Optional<Friend> findByGroupAccountId_GroupAccountId(Long groupAccountId);
}
