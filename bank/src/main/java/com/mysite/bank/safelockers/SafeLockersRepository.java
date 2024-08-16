package com.mysite.bank.safelockers;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.bank.groupaccounts.GroupAccount;

public interface SafeLockersRepository extends JpaRepository<SafeLockers, Long> {
    Optional<SafeLockers> findByGroupAccountId(GroupAccount groupAccountId);

}
