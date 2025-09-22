package com.smartShopper.smart_price_backend.service.user;

import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User create(User user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email exists");
        }
        return repo.save(user);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
}
