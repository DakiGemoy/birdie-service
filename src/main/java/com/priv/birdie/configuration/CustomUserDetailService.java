package com.priv.birdie.configuration;

import com.priv.birdie.entities.AccountUserEntity;
import com.priv.birdie.entities.UserRoleEntity;
import com.priv.birdie.repository.AccountRepository;
import com.priv.birdie.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
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
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        AccountUserEntity account = accountRepository.findByUsername(username).orElseThrow(
                ()->new UsernameNotFoundException("Username not found"));

//
//        if (account.getTwoAuthActive()){ //do something next time
//        }

        List<UserRoleEntity> userRoles = userRoleRepository.findByAccountUser_Id(account.getId());
        List<SimpleGrantedAuthority> grantedAuthorities = userRoles.stream().map(ur->new SimpleGrantedAuthority(ur.getRole().getRoleName())).toList();

        return new User(
                account.getUsername(),
                account.getPassword(),
                account.getIsActive()!=null && account.getIsActive(),
                true,
                true,
                true, // will create bloked-locked mechanism
                grantedAuthorities
        );
    }
}
