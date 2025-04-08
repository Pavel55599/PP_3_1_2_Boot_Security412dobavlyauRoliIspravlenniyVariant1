package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;



    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index").permitAll()



                .antMatchers("/user").hasRole("USER")


                .antMatchers("/admin","/users","/new",
                        "/edit","/update","/delete").hasRole("ADMIN")


                .antMatchers("/show").hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()

                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }




    @Bean
    public CommandLineRunner initUsers(UserService userService,
                                       PasswordEncoder passwordEncoder,
                                       RoleRepository roleRepository) {
        return args -> {
            // Создаем роли, если их нет

            Role adminRole = createRoleIfNotExists("ROLE_ADMIN", roleRepository);
            Role userRole = createRoleIfNotExists("ROLE_USER", roleRepository);

            // Создаем администратора
            createUserIfNotExists(
                    "admin",
                    "admin",  // пароль будет закодирован
                    "Adminov",
                    Set.of(adminRole),//, userRole),  // админ может иметь и роль пользователя
                    userService,
                    passwordEncoder
            );

            // Создаем обычного пользователя
            createUserIfNotExists(
                    "user",
                    "user",  // пароль будет закодирован
                    "Userov",
                    Set.of(userRole),
                    userService,
                    passwordEncoder
            );
        };
    }

    private Role createRoleIfNotExists(String roleName, RoleRepository roleRepository) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    private void createUserIfNotExists(String username,
                                       String password,
                                       String lastName,
                                       Set<Role> roles,
                                       UserService userService,
                                       PasswordEncoder passwordEncoder) {
        if (userService.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));  // кодируем пароль
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setRoles(roles);
            userService.save(user);
        }
    }




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


   @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService((UserDetailsService) userService);


        return authenticationProvider;
   }

}







// аутентификация inMemory
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("user")
//                        .password("user")
//                        .roles("USER")
//                        .build();
//
//        UserDetails admin =
//                User.withDefaultPasswordEncoder()
//                        .username("admin")
//                        .password("admin")
//                        .roles("ADMIN")
//                        .build();
//
//
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }






// аутентификация чз сохранение в базе вручную чз sql - запрос
//      @Bean
//      public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
//
//
//          UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("user")
//                        .password("user")
//                        .roles("USER")
//                        .build();
//
//        UserDetails admin =
//                User.withDefaultPasswordEncoder()
//                        .username("admin")
//                        .password("admin")
//                        .roles("ADMIN")
//                        .build();
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//
//        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(user.getUsername());
//        }
//          if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
//              jdbcUserDetailsManager.deleteUser(admin.getUsername());
//          }
//
//
//        jdbcUserDetailsManager.createUser(user);
//        jdbcUserDetailsManager.createUser(admin);
//        return jdbcUserDetailsManager;
//
//      }