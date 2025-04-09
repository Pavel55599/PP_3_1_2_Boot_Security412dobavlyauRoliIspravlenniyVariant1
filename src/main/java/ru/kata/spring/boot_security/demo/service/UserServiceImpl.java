//package ru.kata.spring.boot_security.demo.service;
//
//
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import ru.kata.spring.boot_security.demo.model.Role;
//import ru.kata.spring.boot_security.demo.model.User;
//import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
//import ru.kata.spring.boot_security.demo.repositories.UserRepository;
//
//import javax.transaction.Transactional;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//
//@Service
//public class UserServiceImpl implements UserService, UserDetailsService {
//
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//
////    @Autowired
////    public UserServiceImpl(UserRepository userRepository) {
////        this.userRepository = userRepository;
////    }
//
//    @Autowired
//    @Lazy
//    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.roleRepository = roleRepository;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = findByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException(String.format("User '%s' not found ", username));
//        }
//
////user.getAuthorities(),
//
//        return new org.springframework.security.core.userdetails.User(user.getUsername(),
//                user.getPassword(), mapRolesToAuthorities(user.getRoles()));
//    }
//
//
//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
//                .collect(Collectors.toList());
//    }
//
//
//    @Override
//    public void save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//
//    }
//}




package ru.kata.spring.boot_security.demo.service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    @Lazy
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        // Если пароль не начинается с BCrypt-префикса, хешируем его
        if (user.getPassword() == null || !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

//    @Override
//    public void save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//
//    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();

    }

//    @Override
//    public void update(Long id, User user) {
//
//               user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//    }




    @Override
    @Transactional
    public void update(Long id, User updatedUser ) {
        // 1. Находим существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // 2. Обновляем username (если изменился)
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(updatedUser.getUsername());
        }

        // 3. Обновляем фамилию (если изменилась)
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().equals(existingUser.getLastName())) {
            existingUser.setLastName(updatedUser.getLastName());
        }

        // 4. Безопасное обновление пароля
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            // 4.1. Проверяем, что новый пароль отличается от текущего
            if (passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword())) {
                throw new IllegalArgumentException("New password must differ from current password");
            }

            // 4.2. Хешируем и сохраняем новый пароль
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // 5. Обновляем роли (если изменились)
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        // 6. Сохраняем обновленные данные
        userRepository.save(existingUser);

//        // 7. Очищаем контекст безопасности (если пользователь авторизован)
//        SecurityContextHolder.clearContext();
    }
























    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);

    }

}
