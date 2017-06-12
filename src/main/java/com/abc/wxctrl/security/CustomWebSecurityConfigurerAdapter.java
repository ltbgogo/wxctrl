package com.abc.wxctrl.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.manager.SpringManager;

@EnableWebSecurity
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	
	@Override
    protected void configure(HttpSecurity security) throws Exception {
    	ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry eiur = security.authorizeRequests();
    	//静态资源控制
    	eiur.antMatchers("/resources/**", "/webjars/**").permitAll();
    	
    	//数据库控制
    	eiur.antMatchers("/h2-console/**").permitAll();
    	
    	 //注册
        eiur.antMatchers("/sso/registration").permitAll();
    	
    	//功能模块权限
    	eiur.antMatchers("/wx/**", "/wx_msg_img/**").hasAuthority(User.USER_AUTHORITY);
        eiur.anyRequest().fullyAuthenticated();
        
    
        //登录
        security.formLogin()
            .loginPage("/sso/login")
            .failureUrl("/sso/login?error")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(new CustomAuthenticationSuccessHandler())
            .permitAll();
        
        //登出权限
        security.logout()
        	.logoutRequestMatcher(new AntPathRequestMatcher("/sso/logout"))
            .deleteCookies("remember-me")
            .logoutSuccessUrl("/")
            .permitAll();
            
        security.rememberMe();
    }
    
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(SpringManager.getBean(DatabaseUserDetailsService.class)).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("test"));
	}
}




