package com.abc.wxctrl.module.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.security.SecurityService;

import static com.abc.wxctrl.repository.RepoFactory.rf;

@RestController
@RequestMapping("/sso")
public class SsoController {
	
	@Autowired
	private SecurityService securityService;

	@RequestMapping("/registration")
	public ModelAndView registration(User user) {
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return new ModelAndView("/sso/registration");
		} else {
			Validate.isTrue(StringUtils.isBlank(user.getId()));
			user = rf.getUserRepo().save(user);
			this.securityService.autologin(user.getUsername(), user.getPassword());
			return new ModelAndView("redirect:/wx/listClients");
		}
	}
}




