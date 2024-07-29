package com.mysite.bank.event;

import com.mysite.bank.users.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EVENTPARTICIPATION")
@SequenceGenerator(name="event_seq", sequenceName="event_seq", initialValue=1, allocationSize=1)
public class Event {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="event_seq")
	private Long participationId;
	
	
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    private String eventName;
    
    private Long eventCount;
    
    private Long additionalInterest;
}
