package com.mysite.bank.users;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository extends JpaRepository<Users, Long>  {
	Optional<Users> findByEmail(String email);
	Optional<Users> findByUserName(String userName);
	Optional<Users> findByUserNickname(String userNickname);
	Optional<Users> findByUserId(Long userId); 
	Optional<Users> findByPassword(String password);
	List<Users> findAll();
}
