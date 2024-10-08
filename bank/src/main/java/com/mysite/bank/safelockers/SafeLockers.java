package com.mysite.bank.safelockers;

import org.hibernate.annotations.ColumnDefault;

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
@Table(name = "SAFE_LOCKER")
@SequenceGenerator(name="safe_locker_seq", sequenceName="safe_locker_seq", initialValue=1, allocationSize=1)
public class SafeLockers {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="safe_locker_seq")
    private Long safelockerId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @ManyToOne
    @JoinColumn(name = "group_account_id", nullable = false)
    private GroupAccount groupAccountId;
    
    @ColumnDefault("0")
    private Long currentBalanceWithInterest = 0L;
    
    @Column(name = "interest_rate", nullable = false)
    private Double interestRate;

}
