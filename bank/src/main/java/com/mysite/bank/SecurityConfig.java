package com.mysite.bank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mysite.bank.friend.FriendService;
import com.mysite.bank.users.UsersRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final UsersRepository usersRepository;
    private final FriendService friendService;
    
    public SecurityConfig(UsersRepository usersRepository, FriendService friendService) {
        this.usersRepository = usersRepository;
        this.friendService = friendService;
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler(usersRepository, friendService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/bank/accountInfo").authenticated()
                .requestMatchers("/bank/checkingAccount").authenticated()
                .requestMatchers("/bank/roulette").authenticated()
                .requestMatchers("/bank/transfer").authenticated()
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin(formLogin -> formLogin
                .loginPage("/bank/login")
                .defaultSuccessUrl("/")
                .successHandler(customSuccessHandler())
                .failureUrl("/bank/login?error=true"))
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/bank/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
