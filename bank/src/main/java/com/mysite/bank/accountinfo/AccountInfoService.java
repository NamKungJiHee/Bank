package com.mysite.bank.accountinfo;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.bank.useraccounts.UserAccounts;
import com.mysite.bank.useraccounts.UserAccountsRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountInfoService {
    private final AccountInfoRepository accountInfoRepository;
    private final UserAccountsRepository userAccountsRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public AccountInfo create(Long accountPassword, String userName) {
        Optional<Users> optionalUser = usersRepository.findByUserName(userName);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Users user = optionalUser.get();
        
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountPassword(accountPassword);
        accountInfo.setAccountNum(generateUniqueAccountNum());
        accountInfo.setBalance(0L);
        accountInfo.setIsGroupaccount(0L);
        accountInfoRepository.save(accountInfo);

        UserAccounts userAccount = new UserAccounts();
        userAccount.setUser(user);
        userAccount.setAccountInfo(accountInfo);
        userAccountsRepository.save(userAccount);

        return accountInfo;
    }

    private String generateUniqueAccountNum() {
        StringBuilder sb = new StringBuilder();
        Random rd = new Random();
        
        for (int i = 0; i < 11; i++) {
            sb.append(rd.nextInt(10));
        }

        if (sb.length() == 11) {
            sb.insert(3, "-").insert(7, "-");
        }

        return sb.toString();
    }

    public List<AccountInfo> findAccountsByUsername(String userName) {
        List<UserAccounts> userAccounts = userAccountsRepository.findByUser_UserName(userName);
        return userAccounts.stream()
                           .map(UserAccounts::getAccountInfo)
                           .toList();
    }
    

}
