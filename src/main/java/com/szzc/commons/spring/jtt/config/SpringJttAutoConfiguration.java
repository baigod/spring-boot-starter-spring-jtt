package com.szzc.commons.spring.jtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.szzc.commons.spring.jtt.jtt.JdbcTemplateProxy;
import com.szzc.commons.spring.jtt.jtt.JdbcTemplateTool;

@Configuration
@ConditionalOnClass({ JdbcTemplateTool.class })
public class SpringJttAutoConfiguration {

	@Autowired(required = false)
	private JdbcTemplate jdbcTemplate;

	@Bean
	public JdbcTemplateTool jdbcTemplateTool() {
		JdbcTemplateTool jdbcTemplateTool = new JdbcTemplateTool();
		jdbcTemplateTool.setJdbcTemplate(jdbcTemplate);
		return jdbcTemplateTool;
	}

	@Bean
	public JdbcTemplateProxy jdbcTemplateProxy() {
		JdbcTemplateProxy jdbcTemplateProxy = new JdbcTemplateProxy();
		jdbcTemplateProxy.setJdbcTemplate(jdbcTemplate);
		return jdbcTemplateProxy;
	}

}