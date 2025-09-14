package com.harshith.dsa_question_picker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DsaQuestionPickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsaQuestionPickerApplication.class, args);
	}

}
