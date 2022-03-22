package app.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.model.Report;
import app.model.User;
import app.service.DateUtils;

@Controller
public class ReportController extends BaseController{
	private static final Logger logger = Logger.getLogger(HomeController.class);
	
	@RequestMapping(value = "/reports")
	public ModelAndView showListReports(HttpSession session) {
		logger.info("Report List Page"); 
		String url = checkAuth("views/reports/report-list");
		ModelAndView model = new ModelAndView(url);
		if (url.contains("redirect")) return model;
		String username = session.getAttribute("currentUser").toString(); 
		model.addObject("currentUser", session.getAttribute("currentUser"));
		
		model.addObject("noReportAlert", messageSource.getMessage("report.noreport", null, Locale.US)); 
		model.addObject("reports", reportService.loadReports(username)); 
		return model; 
	}
	
	@RequestMapping(value = "reports/{id}")
	public ModelAndView showReport(@PathVariable("id") int id, HttpSession session) {
		logger.info("Report Detail" + id); 
		String url = checkAuth("views/reports/report-detail");
		ModelAndView model = new ModelAndView(url);
		if (url.contains("redirect")) return model;
		
		Report report = reportService.findById(id); 
		if (report == null) {
			model.addObject("error", messageSource.getMessage("report.notFound", null, Locale.US));
		}
		else {
			model.addObject("report", reportService.findById(id)); 
			model.addObject("reportNoIssue", messageSource.getMessage("report.content.noIssue", null, Locale.US)); 
			model.addObject("reportNoPlan", messageSource.getMessage("report.content.noPlan", null, Locale.US)); 
		}
		model.addObject("currentUser", session.getAttribute("currentUser"));
		return model; 
	}
	
	@RequestMapping(value = "reports/new")
	public ModelAndView createReport(HttpSession session) {
		logger.info("Create Report");
		
		String url = checkAuth("views/reports/new-report");
		ModelAndView model = new ModelAndView(url);
		if (url.contains("redirect")) return model;
		
		Report report = new Report(); 
		report.setCreatedAt(DateUtils.getToday());
		
		report.setActure(messageSource.getMessage("report.acture.default", null, Locale.US));
		
		model.addObject("currentUser", session.getAttribute("currentUser")); 
		model.addObject("report", report); 
		return model; 
	}
	
	@RequestMapping(value = "/reports/save", method = RequestMethod.POST)
	public String saveOrUpdate(@Valid @ModelAttribute Report report, BindingResult bindingReport,
			final RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		logger.info("POST - Save report");
		if (bindingReport.hasErrors()) {
			logger.error("Binding Error" + bindingReport.getAllErrors()); 
			return "views/reports/new-report";
		}
		
		
		report.setLastUpdatedAt(DateUtils.getToday());  
		User user = userService.findByUsername(session.getAttribute("currentUser").toString()); 
		report.setUser_id(user.getId()); 
		
		Report newReport = reportService.saveOrUpdate(report);	
		if (newReport == null) {
			redirectAttributes.addFlashAttribute("css", "danger");
			redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("report.create.fail", null, Locale.US));
		} else {
			redirectAttributes.addFlashAttribute("css", "success");
			redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("report.create.success", null, Locale.US));
		}
		
		return "redirect:/reports/"+ newReport.getId();
	}
}