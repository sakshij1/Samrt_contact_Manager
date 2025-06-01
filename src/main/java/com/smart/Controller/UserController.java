package com.smart.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
	private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    //Method for adding common data to response
    @ModelAttribute
    public void addCommondata(Model model, Principal principal) {
    	if (principal == null) {
            return; // Redirect if the user is not authenticated
        }

        String userName = principal.getName();
        System.out.println("UserName: " + userName);

        User user = userRepository.getUserByUserName(userName);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return; // Return an error page
        }

        System.out.println(user);
        model.addAttribute("user", user);
    }

    //dashboard home
    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public String dashboard(Model model, Principal principal) {
        
        return "normal/user_dashboard"; // Render the user dashboard
    }
    
    //open add form-handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
    	model.addAttribute("title","Add Contact");
    	model.addAttribute("contact", new Contact());
    	return "normal/add-contact_form";
    }
    
//    //processing add contact form
//    @PostMapping("/process-contact")
//    public String processContact(@ModelAttribute Contact contact, Principal principal) {
//		String name = principal.getName();
//		User user = this.userRepository.getUserByUserName(name);
//		
//		contact.setUser(user);
//		user.getContacts().add(contact);
//		
//		this.userRepository.save(user);
//		
//		System.out.println("Data "+contact);
//		System.out.println("Added data to database");
//    	return "normal/add-contact_form";
//    	
//    }
    
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, 
                                 @RequestParam("imageFile") MultipartFile file, 
                                 Principal principal, HttpSession session) {
        try {
            // Get the current user
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // Process the file
            if (!file.isEmpty()) {
                // Debugging information
                System.out.println("File details:");
                System.out.println("Name: " + file.getOriginalFilename());
                System.out.println("Size: " + file.getSize());
                System.out.println("Type: " + file.getContentType());

                // Generate a unique filename
                String uniqueFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                contact.setImage(uniqueFilename); // Save the filename in the entity

                // Save the file to the static/img directory
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueFilename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File uploaded successfully: " + uniqueFilename);
            } else {
                System.out.println("No file uploaded, setting default image.");
                contact.setImage("contact.png"); // Optional: set a default image
            }

            // Link the contact to the user
            contact.setUser(user);
            user.getContacts().add(contact);

            // Save the user and contact
            this.userRepository.save(user);

            System.out.println("Contact saved successfully: " + contact);
            
            //message success......
            session.setAttribute("message", new Message("Contact added successfully..! Add more!!","alert-success"));
            
            
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("SomeThing went wrong..please try again","alert-danger"));
//            return "error-page"; // Redirect to a user-friendly error page
            //error message.....
            
        }

        return "normal/add-contact_form"; // Redirect to the form page
    }
    
    
    //show contacts handler
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
        m.addAttribute("title", "Show User Contacts");

        // Get the username from the Principal (authenticated user)
        String userName = principal.getName();

        // Get the User object from the username
        User user = this.userRepository.getUserByUserName(userName);

//        // Handle invalid page number (if page is less than 0, set it to 0)
//        if (page < 0) {
//            page = 0;
//        }

        // Pagination: currentPage and number of contacts per page
        Pageable pageable = PageRequest.of(page, 5);

        // Get the paged contacts for the user
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        // Add the contacts and pagination info to the model
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
//        m.addAttribute("totalContacts", contacts.getTotalElements());

        // Return the view
        return "normal/show_contacts";
    }
    
    
    //Showing particular contact detail
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {
    	
    	System.out.println("CID "+cId);
    	
    	Optional<Contact> contactOptional = this.contactRepository.findById(cId);
    	
    	Contact contact = contactOptional.get();
    	
    	//adding security
    	String userName = principal.getName();
    	User user = this.userRepository.getUserByUserName(userName);
    	if(user.getId() == contact.getUser().getId()) {
    		m.addAttribute("contact",contact);
    		m.addAttribute("title", contact.getName());
    	}
    	
    	
    	return "normal/contact_detail";
    	
    }
    
//    //delete contact handler
//    @GetMapping("delete/{cId}")
//    public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session) {
//    	
//    	Optional<Contact> contactOptional = this.contactRepository.findById(cId);
//    	Contact contact = contactOptional.get();
//    	
//    	//check...Assignment...
//    	System.out.println("Contact" +contact.getcId());
//    	
//    	contact.setUser(null);
//    	
//    	
//    	this.contactRepository.delete(contact);
//    	
//    	session.setAttribute("message", new Message("Contact Deleted Successfully...","alert-success"));
//    	
//    	
//    	return "redirect:/user/show-contacts/0";
//    }


    @GetMapping("delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session) {
        // Fetch the contact by ID
        Contact contact = this.contactRepository.findById(cId).orElse(null);
        
        if (contact == null) {
            // If contact not found, return an error message
            session.setAttribute("message", new Message("Contact not found", "alert-danger"));
            return "redirect:/user/show-contacts/0";
        }

        // Fetch the logged-in user
        User user = this.userRepository.getUserByUserName(principal.getName());

        // Log for debugging purposes
        System.out.println("Contact ID: " + contact.getcId());

        // Optionally, disassociate the user from the contact
        user.getContacts().remove(contact);
        
        // Save the updated user data
        this.userRepository.save(user);
        
        // Now, delete the contact from the database
        this.contactRepository.delete(contact);  // This will actually delete the contact from the database
        
        // Set success message in the session
        session.setAttribute("message", new Message("Contact Deleted Successfully...", "alert-success"));
        
        // Redirect to the show contacts page
        return "redirect:/user/show-contacts/0";
    }

    
    
    //open update form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m) {
    	m.addAttribute("title", "Update Contact");
    	
    	Contact contact = this.contactRepository.findById(cid).get();
    	
    	m.addAttribute("contact", contact);
    	
    	return "normal/update_contact";
    }
    
    
    //update contact process handler
    @PostMapping("/update-process-contact")
    public String updateHandler(@ModelAttribute Contact contact, 
    		@RequestParam("imageFile") MultipartFile file, Model m,
            Principal principal, HttpSession session ) {
    	
    	try {
    		//old contact detail
    		Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
    		
    	if(!file.isEmpty()) {
    		//file work
    		//rewrite
    		
    		//delete old photo
    		 File deleteFile = new ClassPathResource("static/img").getFile();
    		 File file1 = new File(deleteFile, oldContactDetail.getImage());
    		 file1.delete();
    		 
    		//update new photo
    		 File saveFile = new ClassPathResource("static/img").getFile();
             Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
             Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    		 contact.setImage(file.getOriginalFilename());
    	}else {
    		contact.setImage(oldContactDetail.getImage());
    	}
    	User user = this.userRepository.getUserByUserName(principal.getName());
    	
    	contact.setUser(user);
    	
    	this.contactRepository.save(contact);
    	
    	session.setAttribute("message", new Message("Contact updated successfully...","alert-success"));
    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("Contact name: "+contact.getName());
    	System.out.println("Id"+contact.getcId());
    	
				return "redirect:/user/"+contact.getcId()+"/contact";
    	
    }
    
    //User Profile Handler
    
    @RequestMapping(value = "/user-profile", method = {RequestMethod.GET, RequestMethod.POST})
    public String userProfile(Model model, Principal principal) {
        model.addAttribute("title", "SCM - User Profile");
        return "normal/user_profile"; // Render the user dashboard
    }

    
    //Setting Handler
    @GetMapping("/settings")
    public String SettingHandler() {
    	return "normal/setting";
    }

    //Change Password...Handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession Session){
        
        System.out.println("Old PASSWORD "+oldPassword);
        System.out.println("NEW PASSWORD "+newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println("Current encrypted password: "+currentUser.getPassword());
        
        if(this.passwordEncoder.matches(oldPassword,currentUser.getPassword())){
            currentUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            Session.setAttribute("message", new Message("Password changed successfully","alert-success"));
        }else{
            Session.setAttribute("message", new Message("Worng password","alert-danger"));
            return "normal/setting";
        }
        return "redirect:/user/index";
        
    }

    
    
    
    
    
}
