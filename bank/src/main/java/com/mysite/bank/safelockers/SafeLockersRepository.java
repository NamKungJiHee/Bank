package com.mysite.bank.safelockers;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.bank.groupaccounts.GroupAccount;
import com.mysite.bank.users.Users;

public interface SafeLockersRepository extends JpaRepository<SafeLockers, Long> {
    Optional<SafeLockers> findByGroupAccountId(GroupAccount groupAccountId);
    List<SafeLockers> findAll();
    List<SafeLockers> findByUser_UserIdAndGroupAccountId_GroupAccountId(Long userId, Long groupAccountId);
    

}
