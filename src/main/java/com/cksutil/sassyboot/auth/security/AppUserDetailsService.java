package com.cksutil.sassyboot.auth.security;

import com.cksutil.sassyboot.user.UserEntity;
import com.cksutil.sassyboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmailId(username);
        return user.map(userEntity -> SessionUser.builder()
                .userId(userEntity.getId())
                .username(username)
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .roles()
                .build()).orElseThrow(()->new UsernameNotFoundException(("User not found with provided credentials.")));
    }
}
