package ru.kata.spring.boot_security.demo.service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;



    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found " ,username));
        }

//user.getAuthorities(),

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),  mapRolesToAuthorities(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


    @Override
    public void save(User user) {
        userRepository.save(user);

    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();

    }

    @Override
    public void update(Long id, User user) {
        userRepository.save(user);

    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);

    }

}
