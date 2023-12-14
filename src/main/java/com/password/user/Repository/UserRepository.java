package com.password.user.Repository;

import com.password.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(nativeQuery = true,value = "SELECT * from newproject.user where email=:email and password=:password")
    User findUser(String email, String password);

//    @Query(nativeQuery = true,value = "SELECT * from newproject.user where email=:email")
    User findByEmail(String email);
}
