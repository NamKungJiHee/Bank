package com.mysite.bank.transfer;

import java.security.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.users.Users;

import jakarta.persistence.Column;
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
@Table(name= "TRANSACTION")
@SequenceGenerator(name="transaction_seq", sequenceName="transaction_seq", initialValue=1, allocationSize=1)
public class Transfer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="transaction_seq")
    private Long transactionId;
	
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @ManyToOne
    @JoinColumn(name = "group_account_id", nullable = false)
    private GroupAccount groupAccountId;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountInfo accountInfo;
    
    @ColumnDefault("0")
    private Long Amount = 0L;
    
    @Column(nullable=false)
    private String transactionType;
    
    private LocalDateTime transactionTime;
    
}
