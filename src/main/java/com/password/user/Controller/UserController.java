package com.password.user.Controller;

import com.password.user.Entity.User;
import com.password.user.Repository.UserRepository;
import com.password.user.Service.UserService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.codehaus.jettison.json.JSONObject;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/save")
    public String saveUser(@RequestBody String input){
        User user = new User();
        User resultUser = user;
        try {
            JSONObject jsonObject = new JSONObject(input);
            String email = jsonObject.getString("email");
            String password =jsonObject.getString("password");
            user.setEmail(email);
            user.setPassword(password);
            resultUser = userService.saveUser(user);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return resultUser.toJsonObject().toString();
    }

    @GetMapping(value = "/getAll")
    public List<User> findUsers(){return userService.findAllUser();}

    @GetMapping(value = "/getById/{id}")
    public User findUserById(@PathVariable("id") int id){ return userService.getUserById(id);}

    @PostMapping(value = "/update/{id}")
    public User updatePassword(@PathVariable("id") int id, @RequestBody User user){
        return userService.updateUser(id,user);
    }

//    update password using entity class
//    @PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String changePassword (@RequestBody User user){
//        User changeUser = userRepository.findUser(user.getEmail(),user.getPassword());
//        if(changeUser.getEmail() == null || changeUser.toString() == null){
//            return "User Not Found";
//        }
//        return userService.updatePassword(changeUser,user.getNewPassword());
//
//    }
//
//    reset using entity class
//    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String resetPassword(@RequestBody User user){
//        User resetUser = userRepository.findUserByEmail(user.getEmail());
//        if(resetUser.getEmail() == null){
//            return "User Not Found";
//        }
//        return userService.resetPassword(resetUser,user.getNewPassword());
//    }
    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable("id") int id){
        return userService.deleteUserById(id);
    }

    @PostMapping(value = "/update")
    public String changePassword(@RequestBody String input){
        try{
            JSONObject jsonObject = new JSONObject(input);
            String email = jsonObject.getString("email");
            String password =jsonObject.getString("password");
            String encryptPassword = decrypt(password);
            String newPassword = jsonObject.getString("newPassword");
            if(email.equalsIgnoreCase("") || password.equalsIgnoreCase("")){
                return "Invalid Email or password ";
            }
            User changeUser = userRepository.findUser(email,encryptPassword);
            if(Objects.nonNull(changeUser)){
                return userService.updatePassword(changeUser,newPassword);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "User Not Found invalid email or password";
    }

    @PostMapping(value = "/reset")
    public String resetPassword(@RequestBody String input){
        try {
            JSONObject jsonObject = new JSONObject(input);
            String email = jsonObject.getString("email");
            String newPassword = jsonObject.getString("newPassword");
            if(email.equalsIgnoreCase("")){
                return "Invalid Email Field Empty";
            }
            User resetUser = userRepository.findByEmail(email);
            if(Objects.nonNull(resetUser)){
                return userService.resetPassword(resetUser,newPassword);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "User Not Found invalid email";
    }

    public String decrypt(String password){
        byte[] encryptedByte = password.getBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedByte);
    }

    @PostMapping(value = "/upload")
    public String uploadFile(@RequestParam("csvfile") MultipartFile multipartFile){
        String response = "not uploaded";
        try{
            if (!multipartFile.isEmpty()) {
                response = userService.readData(multipartFile);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }

    @PostMapping("/convert")
    public ResponseEntity<String> convertPdfToHtml(@RequestParam("file") MultipartFile file) throws IOException, IOException {
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            return ResponseEntity.badRequest().body("Invalid file format");
        }

        String [] name = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String fileName = "D:\\Spring Boot\\user\\src\\output\\"+name[0]+".html";
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFDomTree parser = new PDFDomTree();
        Writer output = new PrintWriter(fileName, StandardCharsets.UTF_8);
        parser.writeText(document, output);
        output.close();
        document.close();
        return ResponseEntity.ok(document.toString());
    }

}
