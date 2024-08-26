package com.mysite.bank.friend;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FRIEND_INVITATION")
@SequenceGenerator(name="friend_invitation_seq", sequenceName="friend_invitation_seq", initialValue=1, allocationSize=1)
public class Friend {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="friend_invitation_seq")
	private Long invitationId;
	
    @ManyToOne
    @JoinColumn(name = "inviter_user_id", nullable = false)
    private Users inviterUserId;
    
    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private Users invitedUserId;
    
    @Column(name = "status", columnDefinition = "VARCHAR2(20) DEFAULT 'PENDING'")
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "group_account_id", nullable = false)
    private GroupAccount groupAccountId;
}
