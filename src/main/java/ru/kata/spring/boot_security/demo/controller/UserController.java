package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String indexUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/indexuserlist";
    }

    @GetMapping("/show")
    public String show(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "users/show";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());

        model.addAttribute("allRoles", roleService.getAllRoles());
        return "users/new";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "selectedRoles", required = false) Set<Long> selectedRoles) {
        Set<Role> roles = new HashSet<>();
        if (selectedRoles != null) {
            for (Long roleId : selectedRoles) {
                roles.add(roleService.getRoleById(roleId));
            }
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "users/edit";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam(value = "selectedRoles", required = false) Set<Long> selectedRoles) {
        Set<Role> roles = new HashSet<>();
        if (selectedRoles != null) {
            for (Long roleId : selectedRoles) {
                roles.add(roleService.getRoleById(roleId));
            }
        }
        user.setRoles(roles);
        userService.update(id, user);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String showDeleteForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/delete";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/users";
    }
}



//package ru.kata.spring.boot_security.demo.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import ru.kata.spring.boot_security.demo.model.User;
//import ru.kata.spring.boot_security.demo.service.UserService;
//
//
//@Controller
//@RequestMapping("/users")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//
//    @GetMapping
//    public String indexUsers(Model model) {
//        model.addAttribute("users", userService.findAll());
//        return "users/indexuserlist";
//    }
//
//
//    @GetMapping("/show")
//    public String show(@RequestParam("id") Long id, Model model) {
//        model.addAttribute("user", userService.findById(id));
//        return "users/show";
//    }
//
//
//    @GetMapping("/new")
//    public String newUser(Model model) {
//        model.addAttribute("user", new User());
//        return "users/new";
//    }
//
//    @PostMapping
//    public String createUser(@ModelAttribute("user") User user) {
//        userService.save(user);
//        return "redirect:/users";
//    }
//
//    // Редактирование пользователя
//    @GetMapping("/edit")
//    public String editUser(@RequestParam("id") Long id, Model model) {
//        model.addAttribute("user", userService.findById(id));
//        return "users/edit";
//    }
//
//
//    // Обновление пользователя
//    @PostMapping("/update")
//    public String updateUser(@RequestParam("id") Long id, @ModelAttribute("user") User user) {
//        userService.update(id, user);
//        return "redirect:/users";
//    }
//
//    // Отображение страницы удаления
//    @GetMapping("/delete")
//    public String showDeleteForm(@RequestParam("id") Long id, Model model) {
//        User user = userService.findById(id);
//        model.addAttribute("user", user);
//        return "users/delete";
//    }
//
//    // Удаление пользователя
//    @PostMapping("/delete")
//    public String deleteUser(@RequestParam("id") Long id) {
//        userService.delete(id);
//        return "redirect:/users";
//    }
//}






























