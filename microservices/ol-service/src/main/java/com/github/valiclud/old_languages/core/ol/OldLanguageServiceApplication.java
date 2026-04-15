package com.github.valiclud.old_languages.core.ol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.github.valiclud")
public class OldLanguageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OldLanguageServiceApplication.class, args);
	}

}
