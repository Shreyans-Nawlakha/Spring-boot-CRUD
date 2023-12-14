package com.password.user.Service;

import com.password.user.Entity.User;
import netscape.javascript.JSObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User saveUser (User user);

    List<User> findAllUser();

    User getUserById(int id);

    User updateUser(int id, User user);

    String updatePassword(User user, String newPassword);

    String resetPassword(User user, String newPassword);

    String deleteUserById(int id);

    String readData(MultipartFile multipartFile);
}
