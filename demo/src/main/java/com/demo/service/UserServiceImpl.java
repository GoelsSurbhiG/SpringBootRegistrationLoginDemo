package com.demo.service;


import com.demo.Repository.PasswordResetTokenRepository;
import com.demo.Repository.UserRepository;
import com.demo.Repository.VerificationTokenRepository;
import com.demo.entity.PasswordResetToken;
import com.demo.entity.User;
import com.demo.entity.VerificationToken;
import com.demo.model.SignUpModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(SignUpModel userModel) {
        if(userRepository.findByEmail(userModel.getEmail())==null) {
            User user = new User();
            user.setEmail(userModel.getEmail());
            user.setUsername(userModel.getUsername());
            //user.setLastName(userModel.getLastName());
            user.setRole("USER");
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));

            userRepository.save(user);
            return user;
        }
        return null;

    }

    @Override
    public void saveToken(String tokenString, User user) {
        VerificationToken verificationToken = new VerificationToken(user, tokenString);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String verifyRegistrationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            return "invalid";
        }

        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }


        User user = verificationToken.getUser();
        if(user.isEnabled()) {
            return "Already Verified User. No Need of Token Verification Anymore";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken resendNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    public void savePasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String verifyPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            return "invalid";
        }


        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime()  - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }
        User user = passwordResetToken.getUser();
        return "valid";
    }

    @Override
    public void saveNewPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkPassword(User user, String password) {

        return passwordEncoder.matches(password, user.getPassword());
    }



}
