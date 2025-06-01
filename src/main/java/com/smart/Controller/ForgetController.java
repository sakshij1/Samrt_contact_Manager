package com.smart.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Service.EmailService;
import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ForgetController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
    Random random = new Random(1000);

    @RequestMapping("/forget")
    public String openEmailFOrm() {
        return "forgot_email_form";
    }
    
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        System.out.println("EMAIL: "+email);
        
        int otp = random.nextInt(999999);

        System.out.println("OTP "+otp);
        User user = this.userRepository.getUserByUserName(email);
        
        
        
        if(user != null) {
        	String subject = "OTP Form SCM";
            String message= ""
            		+"<div style='border:1px solid #e2e2e2; padding:20px'>"
            		+"<h1>Hi "+user.getName()
            		+","
            		+"<br>"
            		+"OTP "
            		+"<b>"+otp
            		+"</h1>"
            		+"</div>";
            		
            String to = email;
        	boolean flag = this.emailService.sendEmail(subject, message, to);
            if(flag) {
          	  session.setAttribute("myotp", otp);
          	  session.setAttribute("email", email);
          	  return "varify_otp";
           }
           else {
          	  session.setAttribute("message", new Message("Check your email id !!","alert-danger"));
          	  return "forgot_email_form";
           }
        }else {
           session.setAttribute("message", new Message("Email not registerd..Kindly check your email id","alert-danger"));
           return "forgot_email_form";
        }
        
        
    }
    
    //Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") Integer otp, HttpSession session) {
        // Retrieve the OTP and email from the session
        Integer myotp = (Integer) session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");

        // Log the OTP for debugging
        System.out.println("Session OTP: " + myotp);
        System.out.println("Entered OTP: " + otp);
        
        if (myotp != null && myotp.equals(otp)) {
            // OTP matches, redirect to password change page
            return "change_password_form";
        } else {
            // OTP doesn't match, display error message
            session.setAttribute("message", new Message("You have entered a wrong OTP.", "alert-danger"));
            System.out.println("OTP mismatch.");
            return "varify_otp";  // Redirect back to OTP verification page
        }
    }
    
    @PostMapping("/change-userPassword")
    public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        
        // Check if email exists in session
        if (email == null) {
            return "redirect:/login"; // Redirect to login page if user is not logged in
        }

        // Retrieve the user by their email
        User user = this.userRepository.getUserByUserName(email);
        
        if (user == null) {
            // Handle user not found case
            return "redirect:/login"; // Or show an error page
        }

        // Encode and set the new password
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        
        // URL-encode the success message to avoid issues with special characters
        
        
        return "redirect:/login?change=Password changed.."; // Redirect with encoded success message
    }



    
}
