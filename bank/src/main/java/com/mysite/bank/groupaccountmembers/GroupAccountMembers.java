package com.mysite.bank.groupaccountmembers;

import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.users.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "GROUP_ACCOUNT_MEMBER")
@SequenceGenerator(name="group_account_member_seq", sequenceName="group_account_member_seq", initialValue=1, allocationSize=1)
public class GroupAccountMembers {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="group_account_member_seq")
    private Long groupAccountMemberId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "groupAccountId", nullable = false)
    private GroupAccount groupAccountId;
}

