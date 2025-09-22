package com.smartShopper.smart_price_backend.service.user;

import com.smartShopper.smart_price_backend.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    List<User> findAll();
    Optional<User>findByEmail(String email);

}
