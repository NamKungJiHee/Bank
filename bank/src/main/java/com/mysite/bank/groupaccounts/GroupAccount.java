package com.mysite.bank.groupaccounts;

import org.hibernate.annotations.ColumnDefault;

import com.mysite.bank.accountinfo.AccountInfo;

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
@Table(name = "GROUP_ACCOUNT")
@SequenceGenerator(name="group_account_seq", sequenceName="group_account_seq", initialValue=1, allocationSize=1)
public class GroupAccount {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="group_account_seq")
    private Long groupAccountId;
	
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountInfo accountInfo;
    
    @Column(nullable=false)
    private String groupName;
    
    @ColumnDefault("0")
    private Long balance = 0L;
    
    @Column(nullable=false)
    private String safelockerType;
    
    @Column(nullable=false)
    private Long safelockerThreshold;
    
    @Column(nullable=false)
    private Long alertThreshold;
}
