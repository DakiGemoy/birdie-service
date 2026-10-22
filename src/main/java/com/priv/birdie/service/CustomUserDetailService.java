package com.priv.birdie.service;

import com.priv.birdie.configuration.ProcessException;
import com.priv.birdie.entities.AccountUserEntity;
import com.priv.birdie.entities.UserRoleEntity;
import com.priv.birdie.repository.AccountRepository;
import com.priv.birdie.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUserEntity account = accountRepository.findByUsernameAndIsActiveTrue(username).orElseThrow(()->new ProcessException(HttpStatus.NOT_FOUND,"04","Username not found / inactive"));

//        if (!account.getIsActive()){ //do something next time
//        }
//
//        if (account.getTwoAuthActive()){ //do something next time
//        }

        List<UserRoleEntity> userRoles = userRoleRepository.findByAccountUser_Id(account.getId());

        List<SimpleGrantedAuthority> grantedAuthorities = userRoles.stream().map(ur->new SimpleGrantedAuthority(ur.getRole().getRoleName())).toList();
        return new User(
                account.getUsername(),
                account.getPassword(),
                grantedAuthorities
        );
    }
}
