package app.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import app.model.User;
import app.service.ReportService;
import app.service.UserService;

@Controller
public class HomeController extends BaseController{
	private static final Logger logger = Logger.getLogger(HomeController.class);

	@RequestMapping(value = "/")
	public ModelAndView index() {
		logger.info("Home page");
		ModelAndView model = new ModelAndView("views/dailyreport/index");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (checkAuth(auth)) {
			model.addObject("currentUser", auth.getName());
		}
		else return new ModelAndView("redirect:/login");
		return model;
	}
	
	@RequestMapping("/login")
	public String login(@RequestParam(value = "error", required = false) final String error, final Model model) {
		logger.info("login page");
		if (error != null) {
			model.addAttribute("css", "error");
			model.addAttribute("msg", messageSource.getMessage("login.fail", null, Locale.US));
		}
		return "/views/users/login";
	}
	
	@RequestMapping("logout")
	public String logout(final Model model) {
		logger.info("login page");
		model.addAttribute("msg", messageSource.getMessage("logout.success", null, Locale.US));
		return "/views/users/login";
	}

}
