package de.bp2019.pusl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * Class containing the main method bootstraping the spring application
 * 
 * @author Leon Chemitz
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class PuslApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuslApplication.class, args);
	}

}
