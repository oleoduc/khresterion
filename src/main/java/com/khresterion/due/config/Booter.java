package com.khresterion.due.config;

import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.user.RoleEntity;
import com.khresterion.web.user.UserEntity;
import com.khresterion.web.user.services.RoleService;
import com.khresterion.web.user.services.UserService;

@Configuration
public class Booter {

	private static final KhresterionLogger LOG = KhresterionLogger
			.getLogger(com.khresterion.due.config.Booter.class);

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@PostConstruct
	public void constructContext() {

		initializeAdmin();
	}

	/**
	 * add a default admin
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	private void initializeAdmin() {
		try {
			LOG.warning("Creating security profiles...");

			Set<RoleEntity> roleList = roleService.list();
			// System.out.println(roleList.size());
			if (roleList.isEmpty()) {
				roleService.createRole("ROLE_USER", "User", null);				
				roleService.createRole("ROLE_EXPERT", "Manager", null);				
				LOG.warning("Security profiles created");
			} else {
				LOG.warning("Security profiles [ok]");
			}

			LOG.warning("Creating admin members...");
			UserEntity adminUser = userService.findByName("root");
			if (adminUser == null) {
				adminUser = userService.createUser("root", "superuser", "super user", null, null);
				userService.addRole("root", "ROLE_EXPERT");
				userService.addRole("root", "ROLE_MANAGER");				
				LOG.warning("Admin members created [ok]");
			} else {
				LOG.warning("Member table [ok]");
			}

		} catch (org.springframework.orm.jpa.JpaSystemException jpe) {
			LOG.warning("Error querying Account table " + jpe.getMessage() + "\n"
					+ new String((jpe.getCause() != null) ? jpe.getCause().getMessage() : "persistence error"));
			jpe.printStackTrace();
		}
	}

}
