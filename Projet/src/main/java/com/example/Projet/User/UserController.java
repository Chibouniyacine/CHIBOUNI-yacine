package com.example.Projet.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.mail.MessagingException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private EmailSenderService senderService;
    static User userG;

//---------------Page index---------------------------//

    @GetMapping("/index/apropos")
    public String apropos(){
        return "/apropos";
    }

    @GetMapping("/index/contact")
    public String contact(){
        return "/contact";
    }

    @GetMapping("/index/Objects")
    public String showUsersList(Model model){
        List<User> listUsers= service.affichage();
        model.addAttribute("listUsers",listUsers);
        return "/ObjectsIndex";
    }

    @GetMapping("/index/inscreption")
    public String inscreption(Model model){
        model.addAttribute("user", new User());
        return "/inscreption";
    }

    //---------------Page ObjectsIndex---------------------------//

    @GetMapping("/ObjectsIndex/inscreption")
    public String Objectsinscreption(Model model){
        model.addAttribute("user", new User());
        return "/inscreption";
    }

    @GetMapping("/ObjectsIndex/acceuil")
    public String ObjectsIndexacceuil(){
        return "/index";
    }

    //---------------Page Inscreption---------------------------//


    @PostMapping("/inscreption/save")
    public String inscreptionUser( User user,Model model,Model model1)
    {   if (user.getName().equals("Amine")) user.setAdmin(true);
        service.save(user);
        userG=user;
        model.addAttribute("mail", new EmailClient());
        model1.addAttribute("user", user);
        if (user.isAdmin()) return "/ProfilAdmin";
        else return "/ProfilClient";
    }

    @PostMapping("/inscreption/save1")
    public String login( User user,Model model,Model model1)
    {   model.addAttribute("mail", new EmailClient());
        model1.addAttribute("user", user);

        if (service.login(user.getEmail(),user.getPassword()))
        {   User user1=service.find_user_by_mail(user.getEmail());
            userG=user1;
            if (user1.isAdmin()) return "/ProfilAdmin";
            else return "/ProfilClient";}
        else return "/index";
    }

    //--------------- Page Profil Admin---------------------------//


    @PostMapping("/ProfilAdmin/sup")
    public String delete ( User user,Model model,Model model1)
    {
        user=service.find_user_by_mail(user.getEmail());
        service.delete(user);
        model.addAttribute("user", userG);
        model1.addAttribute("mail", new EmailClient());
        return "/ProfilAdmin" ;
    }

    @PostMapping("/Profil_Admin/newmail")
    public String processForm(EmailClient mail,Model model,Model model1) throws MessagingException {
        triggerMail(mail.getEmail(),mail.getMessage());
        model.addAttribute("mail", new EmailClient());
        model1.addAttribute("user", userG);
        return "/ProfilAdmin" ;
    }

    public void triggerMail(String mail, String message) throws MessagingException {
        senderService.sendSimpleEmail(mail,
                "Trader Application",
                message);

    }

    @GetMapping("/Profil_Admin/exit")
    public String exitAdmin(){
        userG=null;
        return "/index";
    }


    //---------------Profil Profil Client---------------------------//

    @PostMapping("/ProfilClient/ajout")
    public String addObject(User user,Model model){
        User user1= service.find_user_by_mail(userG.getEmail());
        user1.setNomLivre(user.getNomLivre());
        user1.setPhoto(user.getPhoto());
        service.save(user1);
        model.addAttribute("user", userG);
        return "/ProfilClient";
    }

    @GetMapping("/ProfilClient/voir")
    public String ProfilClientvoir(Model model){
        //model.addAttribute("mail", new EmailClient());
        List<User> listUsers= service.affichage();
        model.addAttribute("listUsers",listUsers);
        return "/Objects";
    }

    @GetMapping("/ProfilClient/exit")
    public String exitClient(){
        userG=null;
        return "/index";
    }

    @GetMapping("/ProfilClient/voirClient")
    public String ProfilClientvoirClient(Model model){
        model.addAttribute("user", new User());
        return "/ConsulteLivre";
    }


    //---------------Page Objects---------------------------//

@GetMapping("/Objects/demande")
public String Objectsdemande ( Model model)
   {
    model.addAttribute("mail", new EmailClient());
    return "/Demande" ;
   }

    @GetMapping("/Objects/retour")
    public String ObjectsRetour( Model model)
    {
        model.addAttribute("user",  userG);
        return "/ProfilClient";
    }
    //---------------Page Demande---------------------------//

@PostMapping("/Demande/echange")
public String demande(EmailClient mail,Model model,Model model1) throws MessagingException {

    triggerMail1(mail.getEmail());
    model.addAttribute("mail", new EmailClient());
    model1.addAttribute("user",  userG);
    return "/ProfilClient" ;
}

    public void triggerMail1(String mail) throws MessagingException {
        String message1 =userG.getEmail();
        senderService.sendSimpleEmail(mail,
                "Trader Application",
                "Jaimerais bien faire un echange de livre avec vous ! voici mon email ("+message1+").....Salutations");

    }

    //---------------Page ConsulteLivre---------------------------//


    @PostMapping("/ConsulteLivre/livre")
    public String ConsulteLivrelivre( User user,Model model)
    {   User user1=service.find_user_by_mail(user.getEmail());
        model.addAttribute("user", user1);
        return "/AffichageLivre";
    }
    @GetMapping("/ConsulteLivre/retour")
    public String ConsulteLivreRetour( Model model)
    {
        model.addAttribute("user",  userG);
        return "/ProfilClient";
    }
    //---------------Page AffichageLivre---------------------------//

    @GetMapping("/AffichageLivre/retour")
    public String AffichageLivreRetour( Model model)
    {
        model.addAttribute("user",  userG);
        return "/ProfilClient";
    }
}
