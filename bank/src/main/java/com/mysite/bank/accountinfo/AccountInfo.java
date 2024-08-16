package com.mysite.bank.accountinfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACCOUNT_INFO")
@SequenceGenerator(name="account_info_seq", sequenceName="account_info_seq", initialValue=1, allocationSize=1)
public class AccountInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="account_info_seq")
    private Long accountId;
    
    @Column(unique=true, nullable=false)
    private String accountNum;
    
    @ColumnDefault("0")
    private Long balance = 0L;
    
    @Column(nullable=false)
    private Long accountPassword;
    
    @ColumnDefault("0")
    private Long isGroupaccount = 0L;
}
