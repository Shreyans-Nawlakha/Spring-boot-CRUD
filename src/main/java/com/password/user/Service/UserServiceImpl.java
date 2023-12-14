package com.password.user.Service;

import com.password.user.Entity.User;
import com.password.user.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return  optionalUser.orElse(null);
    }

    @Override
    public User updateUser(int id, User user) {
        Optional<User> user1 = userRepository.findById(id);

        if(user1.isPresent()){
            User originalUser = user1.get();

            if(Objects.nonNull(originalUser.getEmail()) && !"".equalsIgnoreCase(originalUser.getEmail())){
                originalUser.setEmail(user.getEmail());
            }

            if (Objects.nonNull(originalUser.getPassword()) && !"".equalsIgnoreCase(originalUser.getPassword())){
                originalUser.setPassword(user.getPassword());
            }
            return  userRepository.save(originalUser);
        }
        return null;
    }

    @Override
    public String deleteUserById(int id){
        if(userRepository.findById(id).isPresent()){
            userRepository.deleteById(id);
            return "Employee deleted successfully";
        }
        return "No such employee in the database";
    }

    @Override
    public String updatePassword(User user, String newPassword){
        if(newPassword == null || "".equalsIgnoreCase(newPassword)){
            return "Password Blank or Empty";
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password Changed Successfully";
    }

    @Override
    public String resetPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password Reset Successfully";
    }

    @Override
    public String readData(MultipartFile file){
        String response = "";
        try {
            List<User> userData = getUserList(file);
            if (!userData.isEmpty()){
                userRepository.saveAll(userData);
                response = file.getOriginalFilename()+" read Successfully";
            }
            else {
                response = "File Empty or User exists";
            }

        } catch (IOException e) {
            response = "Data Already read or Cannot write uploaded file to disk!";
            System.out.println(new RuntimeException(e));
        }
        return response;
    }

    private List<User> getUserList(MultipartFile file) throws IOException {
        InputStream fileReader = file.getInputStream();
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(fileReader));

        String output;
        List<User> userData = new ArrayList<>();
        while((output = bfReader.readLine())!= null){
            if (output.contains("email,")) continue;

            String[] value = output.split(",");
            User checkUser = userRepository.findByEmail(value[0]);
            if(Objects.isNull(checkUser)){
                User newUser = new User();
                newUser.setEmail(value[0]);
                newUser.setPassword(value[1]);
                userData.add(newUser);
            }
            else{
                System.out.println("This user Exists: "+checkUser.getEmail());
            }
        }
        return userData;
    }
}
