package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// this is showing login page
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }
	 
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/home")
	public String home1(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
//	//handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid
	        @ModelAttribute("user") User user,BindingResult result1,
	        @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
	        Model model, 
	        HttpSession session) throws Exception {
	    try {
	        if (!agreement) {
	            throw new Exception("You have not agreed to terms and conditions");
	        }
	        
	        if(result1.hasErrors()) {
	        	System.out.println("Error "+result1.toString());
	        	model.addAttribute("user", user);
	        	return "signup";
	        }

	        // Set user properties
	        user.setRole("ROLE_User");
	        user.setEnabled(true);
	        user.setImageUrl("contact.jsp");
	        user.setPassword(passwordEncoder.encode(user.getPassword()));

	        // Save user to the repository
	        User result = userRepository.save(user);

	        // Add user and success message to the model
	        model.addAttribute("user", result);
	        session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
	    } catch (Exception e) {
	        e.printStackTrace();

	        // Add error message and user to the model
	        session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-danger"));
	        model.addAttribute("user", user);
	    }

	    // Retrieve the message from the session
	    Message message = (Message) session.getAttribute("message");
	    model.addAttribute("message", message);

	    // Remove the message from the session
//	    SessionHelper.removeMessage();

	    return "redirect:/signup";
	}


//	@PostMapping("/do_register")
//	public String registerUser(
//	        @ModelAttribute("user") User user,
//	        @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
//	        Model model,
//	        HttpSession session) {
//	    try {
//	        if (!agreement) {
//	            throw new IllegalArgumentException("You have not agreed to the terms and conditions.");
//	        }
//
//	        user.setRole("ROLE_User");
//	        user.setEnabled(true);
//	        user.setImageUrl("default.jpg");
//
//	        User result = userRepository.save(user);
//
//	        session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
//
//	        model.addAttribute("user", result);
//	        model.addAttribute("sessionMessage", session.getAttribute("message")); // Add message explicitly to model
//
//	        return "signup";
//	    } catch (IllegalArgumentException e) {
//	        session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
//	        model.addAttribute("user", user);
//	        model.addAttribute("sessionMessage", session.getAttribute("message")); // Add message explicitly to model
//	        return "signup";
//	    } catch (Exception e) {
//	        session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
//	        model.addAttribute("user", user);
//	        model.addAttribute("sessionMessage", session.getAttribute("message")); // Add message explicitly to model
//	        return "signup";
//	    }
//	}



}
