package hu.homework.developermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "hu.homework.developermanager")
@EntityScan("hu.homework.developermanager.model.entity")
public class DeveloperManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeveloperManagerApplication.class, args);
	}

}
