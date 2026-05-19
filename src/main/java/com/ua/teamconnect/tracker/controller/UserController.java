package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseBadRequest;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseNoContent;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserFullProfileDto;
import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.dto.UserShortProfileDto;
import com.ua.teamconnect.tracker.model.dto.UserUpdateProfileDto;
import com.ua.teamconnect.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Controller", description = "Endpoints related to users")
@RequiredArgsConstructor
@ApiResponseUnauthorized
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @ApiResponseOk(content = @Content(
        schema = @Schema(implementation = UserFullProfileDto.class)
    ))
    public UserProfile profile(@AuthenticationPrincipal Jwt jwt) {
        return userService.profile(jwt.getSubject());
    }

    @GetMapping("/anniversaries")
    @ApiResponseBadRequest @ApiResponseOk
    public List<UserAnniversaryDto> getAnniversariesBetween(
        @Parameter(description = "Start date in dd-MM format", example = "20-01", required = true)
        String startDate,
        @Parameter(description = "End date in dd-MM format", example = "10-02", required = true)
        String endDate
    ) {
        return userService.getAnniversariesBetween(startDate, endDate);
    }

    @GetMapping("/{id}")
    @ApiResponseOk(content = @Content(
        schema = @Schema(oneOf = {UserFullProfileDto.class, UserShortProfileDto.class})
    ))
    public UserProfile getUserById(
        @AuthenticationPrincipal Jwt jwt,
        @Parameter(description = "User id to get", example = "1", required = true)
        @PathVariable Integer id
    ) {
        return userService.getUserById(jwt.getSubject(), id);
    }
    
    @ApiResponseNoContent
	@ApiResponseUnauthorized
	@ApiResponseBadRequest
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping("/profile")
	public void updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserUpdateProfileDto dto) {
		userService.updateProfile(jwt.getSubject(), dto);
	}
}
