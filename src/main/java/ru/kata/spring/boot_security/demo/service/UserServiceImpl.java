package ru.kata.spring.boot_security.demo.service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;


import javax.transaction.Transactional;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

  

    @Override
    @Transactional
    public void save(User user) { //   СОХРАНЕНИЕ/СОЗДАНИЕ
        userDao.save(user);

    }

    @Override
    public User findById(Long id) {//  ПОИСК ПО ИД   /show
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {//  ЧТЕНИЕ   /indexuserlist
        return userDao.findAll();
    }

    @Override
    @Transactional
    public void update(Long id,User user) {//   ОБНОВЛЕНИЕ/edit/PATCH-запрос
        userDao.update(id, user);

    }

    @Transactional
    @Override
    public void delete(Long id) {//   УДАЛЕНИЕ
        userDao.delete(id);

    }
}
