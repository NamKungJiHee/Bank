package com.mysite.bank.event;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mysite.bank.users.ApplicationEnvironmentConfig;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.useraccounts.UserAccountsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EventService {
	private final EventRepository eventRepository;
    private final UserAccountsRepository userAccountsRepository;
    private final UsersRepository usersRepository;
    
	// 횟수, 이벤트이름, 사용자 
	public Event eventInfo(String userName, String eventName, Long numEvent) {
		 Optional<Users> optionalUser = usersRepository.findByUserName(userName);
	        if (!optionalUser.isPresent()) {
	            throw new RuntimeException("User not found");
	        }
	        Users user = optionalUser.get();
	        
	        Event event = new Event();
	        event.setUser(user);
	        event.setEventName(eventName);
	        event.setEventCount(numEvent);
	        event.setAdditionalInterest((long) 0);
	        eventRepository.save(event);
	        
	        return event;
	}
	
	// 이벤트 참여 기록
	 public boolean hasUserParticipated(String userName) {
	        return eventRepository.findByUser_UserName(userName).isPresent();
	    }
	
	 // 이벤트 당첨 기록
	 public Optional<Event> eventName(String userName) {
		 return eventRepository.findByUser_UserName(userName);
		 
	 }
	 
}
