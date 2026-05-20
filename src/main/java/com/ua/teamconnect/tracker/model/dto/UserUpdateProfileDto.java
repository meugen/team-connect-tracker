package com.ua.teamconnect.tracker.model.dto;

import java.util.Map;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Update user profile", description = "Data for updating user profile")
public record UserUpdateProfileDto (

		@Schema(description = "User avatar URL", example = "https://avatar.com/avatar.png")
		@URL
		String avatar, 
		
		@Schema(description = "User phone numbers", example = "{\"work\": \"+380697554332\", \"home\": \"+380441234567\"}")
		@Valid
		Map<@Pattern(regexp = "^(work|home|mobile)$", message = "Valid keys: work, home, mobile") String,
			@Pattern(regexp = "^\\+[0-9]\\d{7,14}$", message = "Invalid phone number") String> phone, 
			
		@Schema(description = "New user password", example = "new_password")
		@Pattern(
			    regexp = "^$|^.{3,25}$",
			    message = "Password must be between 3 and 25 characters long."
			)
		String password
	
) {
}
