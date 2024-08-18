package com.mysite.bank.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;

import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;
import com.mysite.bank.DataNotFoundException;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.safelockers.SafeLockers;
import com.mysite.bank.safelockers.SafeLockersRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
@RequiredArgsConstructor
@Service
@EnableScheduling
public class MailService {
	
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final SafeLockersRepository safeLockersRepository;
	
	// 임시비밀번호로 업데이트
	public void updatePassword(String pwd, String email) {
		String tempPassword = pwd;
		Long userId = this.usersRepository.findByEmail(email).get().getUserId();
		String userName = this.usersRepository.findByUserId(userId).get().getUserName();
		String userNickname = this.usersRepository.findByUserId(userId).get().getUserNickname();
		Users user = new Users();
		user.setUserId(userId);
		user.setPassword(passwordEncoder.encode(tempPassword));
		user.setUserName(userName);
		user.setEmail(email);
		user.setUserNickname(userNickname);
		this.usersRepository.save(user);
	}
	
	//랜덤함수로 임시비밀번호 구문
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        String str = "";
        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
    
    // 메일 보내기
    @Autowired
    JavaMailSender mailSender;
    
    public void mailSend(MailDTO mailDTO) { // 임시비밀번호 & safeLocker알림 서비스
		 System.out.println("===== 이메일 전송 =====");
		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(mailDTO.getAddress());
		 message.setSubject(mailDTO.getTitle());
		 message.setText(mailDTO.getMessage());
		 message.setFrom("jihee9711@naver.com");
//		 message.setReplyTo("보낸이@naver.com");
		 System.out.println("message:: "+message);
		 mailSender.send(message);
    }
    
    // 메일 내용을 생성하고 임시비번으로 사용자 비번 변경
    public MailDTO createMailChangePwd(String email) {
    	String tempPwd = getTempPassword();
    	MailDTO dto = new MailDTO();
    	dto.setAddress(email); // 이메일
    	dto.setTitle("게시판 임시비밀번호입니다.");
    	dto.setMessage("임시 비밀번호는 " + tempPwd + "입니다.");
    	updatePassword(tempPwd, email); // 임시 비번으로 바뀜
    	return dto;
    }
    
	// 새 비밀번호로 업데이트
	public void updateNewPassword(String userName, String bPwd, String aPwd) {
		String tempPassword = bPwd; // 임시 비번
		String newPassword= aPwd; // 새 비번
		
		Long userId = this.usersRepository.findByUserName(userName).get().getUserId();
		String userEmail = this.usersRepository.findByUserName(userName).get().getEmail();
		String userNickname = this.usersRepository.findByUserName(userName).get().getUserNickname();
		Users user = new Users();
		user.setUserId(userId);
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUserName(userName);
		user.setEmail(userEmail);
		user.setUserNickname(userNickname);
		this.usersRepository.save(user);
	}
	
	
	// safeLocker에 사용자가 설정한 금액이 모이면 알림(이메일) 보내는 서비스
	// 주기적으로 조건을 확인하고 이메일 전송
	 @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
//	@Scheduled(cron = "0 */1 * * * *") // 1분마다 실행 (Test)
	public void checkAndSendEmails() {
	    // 모든 SafeLocker 조회
	    List<SafeLockers> allLockers = safeLockersRepository.findAll();

	    // 조건에 맞는 사용자 필터링
	    List<Users> usersToAlert = new ArrayList<>();

	    for (SafeLockers locker : allLockers) {
	        Long alertThreshold = locker.getGroupAccountId().getAlertThreshold();
	        Long currentBalance = locker.getCurrentBalanceWithInterest();

	       // System.out.println("#alert:: " + alertThreshold);
	       // System.out.println("#currentBalance:: " + currentBalance);

	        if (currentBalance >= alertThreshold) {
	            System.out.println("SafeLocker 알림서비스");
	            Users user = locker.getUser();
	            usersToAlert.add(user);  // 조건에 맞는 사용자 추가
	        } else {
	            System.out.println("SafeLocker 조건 충족 x");
	        }
	    }

	    // 필터링된 사용자들에게 이메일 전송
	    for (Users user : usersToAlert) {
	        Long userId = user.getUserId();
	        //System.out.println("#UserId::  " + userId);
	        // 이메일 내용 생성
	        MailDTO mailDTO = createMailForThreshold(userId);
	        // 이메일 전송
	        mailSend(mailDTO);
	    }
	}

	    private MailDTO createMailForThreshold(Long userId) {
	    	
	    	Optional<Users> users = this.usersRepository.findById(userId);
	    	String email = users.get().getEmail();
	    	// System.out.println("#Email:: " + email);
	        
	        String message = "SafeLocker에 설정하신 금액이 모였습니다. 확인해주세요!!";
	        MailDTO dto = new MailDTO();
	        dto.setAddress(email);
	        dto.setTitle("SafeLocker 알림 - 쥬디뱅크");
	        dto.setMessage(message);
	        return dto;
	    }
}