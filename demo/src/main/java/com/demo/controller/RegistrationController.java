package com.demo.controller;

import java.util.UUID;

import com.demo.model.LoginModel;
import com.demo.model.UpdatePasswordModel;
import com.demo.service.EmailSenderService;
import jakarta.mail.MessagingException;
import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogAccessor;
import org.springframework.web.bind.annotation.*;

import com.demo.entity.User;
import com.demo.entity.VerificationToken;
import com.demo.model.SignUpModel;
import com.demo.service.UserService;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RegistrationController {
	
	@Autowired
	private UserService userService;

	@Autowired
	EmailSenderService emailSenderService;
	@PostMapping("/register")
	public String registerUser(@RequestBody SignUpModel userModel)
			//throws MessagingException
			{
		
		User user = userService.registerUser(userModel);

		if(user==null) {
			log.info("Email Id Already Registered.");
			return "Email Id Already Registered.";
		}
		
		String tokenString = UUID.randomUUID().toString();
		userService.saveToken(tokenString,user);
		//Send Mail Logic

		//emailSenderService.sendSimpleEmail(user.getEmail(), "Please Verify this Token  " + tokenString,"Verification Token");
		//triggerMail(user,tokenString);
		log.info(String.format("Registration Verification Token %s" ,tokenString));
		return "Registration Success";

	}
	
	@GetMapping("/verifyRegistration")
	public String verifyRegistrationToken(@RequestParam String token) {
		
		String result = userService.verifyRegistrationToken(token);
		
		
		if(result.equalsIgnoreCase("valid"))
				return "User Verification Success";  
		else if(result.equalsIgnoreCase("expired"))
			return "Verification Token Expired";
		else {
			log.info("Valid Token");
			return result;
		}

	}
	
	@GetMapping("/resendVerifyToken")
	public String resendNewVerificationToken(@RequestParam("token") String oldToken) {

		VerificationToken verificationToken
				= userService.resendNewVerificationToken(oldToken);

		if(verificationToken == null) {
			log.info("Invalid Old Token. Kindly Register Yourself");
			return "Invalid Old Token. Kindly Register Yourself";
		}
		User user = verificationToken.getUser();

		if(user.isEnabled()) {
			log.info("You are already verified User. No Need of further Verification Token. Please Login");
			return "You are already verified User. No Need of further Verification Token. Please Login.";
		}
		String tokenString = UUID.randomUUID().toString();
		userService.saveToken(tokenString,user);

		//Send Mail Logic
		log.info(String.format("New Registration Verification Token %s" ,tokenString));
		 return "New Verification Token Sent";
	}

	@PostMapping("/forgotPassword")
	public String forgotPassword(@RequestParam("token") String email) {
		User user = userService.findUserByEmail(email);

		if(user!=null) {
			String token = UUID.randomUUID().toString();
			userService.savePasswordResetToken(user,token);

			//Send Mail Logic
			log.info(String.format("Password Reset Token %s" ,token));
			return "Password Reset Token Link Sent";
		}
		log.info("Email Not Exists");
		return "Email Not Exists";
	}

	@PostMapping("/verifyPasswordToken")
	public String verifyPasswordResetToken(@RequestParam("token") String token) {
		String result = userService.verifyPasswordResetToken(token);

		if(result.equalsIgnoreCase("invalid")) {
			return "Invalid Token";
		}

		else if(result.equalsIgnoreCase("expired")) {
			return "Expired Password Reset Token";
		}
		log.info("valid Token");
        return "valid Token";
	}

	@PostMapping("/savePasswordReset")
	public String saveNewPassword(@RequestParam("email") String email,
								  @RequestParam("password") String newPassword) {
		User user = userService.findUserByEmail(email);

		if(user == null) {
			return "This Email is not Registered";
		}
		userService.saveNewPassword(user, newPassword);
		log.info("Password Reset Successful");
			return "Password Reset Successful";
	}

	@PostMapping("/updatePassword")
	public String updatePassword(@RequestBody UpdatePasswordModel passwordModel){

		User user = userService.findUserByEmail(passwordModel.getEmail());
		if(!userService.checkPassword(user,passwordModel.getOldPassword())) {
			return "Invalid Old Password. Please Enter Correct old Password.";
		}
		//Save New Password
		userService.saveNewPassword(user,passwordModel.getNewPassword());
		log.info("Password Updated Successfully");
		return "Password Updated Successfully";
	}

	public void triggerMail(User user, String tokenString) throws MessagingException {
		emailSenderService.sendSimpleEmail(user.getEmail(), "Please Verify this Token  " + tokenString,"Verification Token");
	}

	@PostMapping("/login")
	public String login(@RequestBody LoginModel loginModel){
		User user = userService.findUserByEmail(loginModel.getEmail());

		if(user == null) {
			log.info("Email does not Exist");
			return "This Email does not Exist. Please Register.";
		}

		if(!userService.checkPassword(user,loginModel.getPassword())) {
			log.info("Incorrect Password");
			return "Incorrect Password. Please Enter Correct Password.";
		}
		//Authenticated User
		log.info("logged In");
		return "Welcome. You are now logged In.";
	}


}

