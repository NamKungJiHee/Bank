package com.mysite.bank.accountinfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long>{

}
