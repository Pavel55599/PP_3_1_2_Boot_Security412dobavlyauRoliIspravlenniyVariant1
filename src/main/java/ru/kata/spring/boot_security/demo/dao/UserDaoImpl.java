package ru.kata.spring.boot_security.demo.dao;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class UserDaoImpl implements UserDao {


    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void save(User user) {
        entityManager.persist(user);

    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);

    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("from User", User.class).getResultList();

    }


    @Transactional
    @Override
    public void update(Long id, User user) {
        User u = entityManager.find(User.class, id);
        if (u != null) {
            u.setName(user.getName());
            u.setLastName(user.getLastName());
            entityManager.merge(u);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User u = entityManager.find(User.class, id);
        if (u != null) {
            entityManager.remove(u);
        }
    }
}
