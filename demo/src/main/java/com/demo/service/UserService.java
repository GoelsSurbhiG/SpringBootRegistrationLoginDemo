package com.demo.service;

import org.springframework.stereotype.Service;

import com.demo.entity.User;
import com.demo.entity.VerificationToken;
import com.demo.model.SignUpModel;

@Service
public interface UserService {

	

	public User registerUser(SignUpModel userModel);

	public void saveToken(String tokenString, User user);

	public String verifyRegistrationToken(String token);

	public VerificationToken resendNewVerificationToken(String oldToken);


	User findUserByEmail(String email);

	void savePasswordResetToken(User user, String token);

	String verifyPasswordResetToken(String token);

	void saveNewPassword(User user, String newPassword);

	boolean checkPassword(User user, String password);

}
