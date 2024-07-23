package com.mysite.bank.useraccounts;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.users.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(name="useraccounts_seq", sequenceName="useraccounts_seq", initialValue=1, allocationSize=1)
public class UserAccounts {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="useraccounts_seq")
    private Long userAccountId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountInfo accountInfo;
}
