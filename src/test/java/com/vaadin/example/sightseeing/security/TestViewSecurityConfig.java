package com.vaadin.example.sightseeing.security;

import com.vaadin.example.sightseeing.data.service.UserRepository;
import com.vaadin.example.sightseeing.security.AuthenticatedUser;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.ViewAccessChecker;
import com.vaadin.flow.spring.SpringViewAccessChecker;
import com.vaadin.flow.spring.security.ViewAccessCheckerInitializer;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

@Configuration
@Import({ViewAccessCheckerInitializer.class})
class TestViewSecurityConfig {

    @Bean
    UserDetailsService mockUserDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                if ("user".equals(username)) {
                    return new User(username, UUID.randomUUID().toString(),
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_DEV"),
                                    new SimpleGrantedAuthority("ROLE_USER")
                            ));
                }
                if ("admin".equals(username)) {
                    return new User(username, UUID.randomUUID().toString(),
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_SUPERUSER"),
                                    new SimpleGrantedAuthority("ROLE_ADMIN")
                            ));
                }
                throw new UsernameNotFoundException(
                        "User " + username + " not exists");
            }
        };
    }

}

