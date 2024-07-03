package com.mysite.users;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(name="users_seq", sequenceName="users_seq", initialValue=1, allocationSize=1)
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_seq")
	private Long userId;
	
	@Column(unique=true)
	private String userName;
	
	@Column(unique=true)
	private String userNickname;
	
	private String password;
	
	@Column(unique=true)
	private String email;
}
