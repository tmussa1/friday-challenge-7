package com.mc.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    CloudinaryConfig cloudinaryConfig;

    @Autowired
    AppRoleRepository appRoleRepository;

    @RequestMapping("/")
    public String showHomepage(Model model){
        model.addAttribute("items", itemRepository.findAll());
        return "index";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("user", new AppUser());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveuser(@ModelAttribute("user") AppUser user, Model model){
        AppRole role = appRoleRepository.findByRole("USER");
        user.addRole(role);
        appUserRepository.save(user);
        return "index";
    }

    @RequestMapping("/sell")
    public String sellItem(Model model){
        model.addAttribute("item", new Item());
        return "sell";
    }

    @PostMapping("/sell")
    public String processFriend(@ModelAttribute("friend") Item item, Model model, MultipartHttpServletRequest request) throws IOException {
        MultipartFile fi = request.getFile("file");
        if(fi.isEmpty() && item.getPicture().isEmpty()){
            return "index";
        }
        try {
            Map uploadResult = cloudinaryConfig.upload(fi.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String myURL = (String) uploadResult.get("url");
            String uploadName = (String) uploadResult.get("public_id");
            String finalImage = cloudinaryConfig.createUrl(uploadName);
            item.setPicture(finalImage);
            itemRepository.save(item);
        } catch(Exception e){
            e.printStackTrace();
            return "redirect:/sell";
        }
        model.addAttribute("items", itemRepository.findAll());
        return "index";
    }

    @RequestMapping("/buy/{id}")
    public String buy(@PathVariable("id") long id, Model model){
        Item item = itemRepository.findById(id).get();
        model.addAttribute("item", item);
        itemRepository.save(item);
        return "buy";
    }

    @RequestMapping("/changeavailability/{id}")
    public String changeAvailbility(@PathVariable("id") long id, Model model){
        Item item = itemRepository.findById(id).get();
        model.addAttribute("item", item);
        itemRepository.save(item);
        return "sell";
    }

    @PostMapping("/search")
    public String search(HttpServletRequest request, Model model){
        String searchTerm = request.getParameter("search");
        model.addAttribute("search", searchTerm);
        model.addAttribute("items", itemRepository.findAllByDescriptionContainingIgnoreCase(searchTerm));
        return "index";
    }

    @PostConstruct
    public void fillData(){
        AppRole role1 = new AppRole("USER");
        AppRole role2 = new AppRole("ADMIN");
        appRoleRepository.save(role1);
        appRoleRepository.save(role2);

        AppUser user1 = new AppUser();
        user1.addRole(role1);
        user1.setFullname("MUSSA");
        user1.setUsername("user");
        user1.setPassword("password");
        appUserRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.addRole(role2);
        user2.setFullname("TIM");
        user2.setUsername("admin");
        user2.setPassword("password");
        appUserRepository.save(user2);

        Item item3 = new Item();
        item3.setDescription("tata");
        item3.setPicture(null);
        item3.setPrice(345);
        item3.setAvailable(true);
        item3.setDelist(false);
        item3.setUser(user1);
        itemRepository.save(item3);

        Item item4 = new Item();
        item4.setDescription("Huawei");
        item4.setPicture(null);
        item4.setPrice(456);
        item4.setAvailable(false);
        item4.setDelist(true);
        item4.setUser(user2);
        itemRepository.save(item4);

    }
}
