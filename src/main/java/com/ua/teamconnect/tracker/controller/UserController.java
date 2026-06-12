package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseBadRequest;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseNoContent;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public UserProfile findProfile(@AuthenticationPrincipal Jwt jwt) {
        return userService.findProfile(jwt.getSubject());
    }

    @GetMapping("/anniversaries")
    @ApiResponseBadRequest @ApiResponseOk
    public List<UserDateDto> findAnniversariesBetween(
        @Parameter(description = "Start date in dd-MM format", example = "20-01", required = true)
        String startDate,
        @Parameter(description = "End date in dd-MM format", example = "10-02", required = true)
        String endDate
    ) {
        return userService.findAnniversariesBetween(startDate, endDate);
    }

    @GetMapping("/{id}")
    @ApiResponseOk(content = @Content(
        schema = @Schema(oneOf = {UserFullProfileDto.class, UserShortProfileDto.class})
    ))
    public UserProfile findUserById(
        @AuthenticationPrincipal Jwt jwt,
        @Parameter(description = "User id to get", example = "1", required = true)
        @PathVariable Integer id
    ) {
        return userService.findUserById(jwt.getSubject(), id);
    }

    @GetMapping
    @ApiResponseOk
    @Parameter(name = "search", description = "Search query for filtering users by name", example = "John")
    @Parameter(name = "department", description = "Comma separated department id(s) for filtering users", example = "1,2,3")
    @Parameter(name = "position", description = "Comma separated position id(s) for filtering users", example = "1,2,3")
    @Parameter(name = "stack", description = "Comma separated stack id(s) for filtering users", example = "1,2,3")
    @Parameter(name = "page", description = "Page number for pagination (starting from 1)", example = "1")
    @Parameter(name = "size", description = "Page size for pagination", example = "10")
    @Parameter(name = "sort", description = "Sorting field", example = "firstName")
    @Parameter(name = "order", description = "Sorting order (asc or desc)", example = "asc")
    public PageDto<UserDto> findFiltered(@RequestParam @Parameter(hidden = true) Map<String, String> params) {
        return userService.findFiltered(params);
    }

    @ApiResponseNoContent
    @ApiResponseBadRequest
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserUpdateProfileDto dto) {
        userService.updateProfile(jwt.getSubject(), dto);
    }

    @GetMapping("/new-hires")
    @ApiResponseOk
    public List<UserDateDto> findNewHires() {
        return userService.findNewHires();
    }
    
    @GetMapping("/birthdays")
    @ApiResponseOk
    @ApiResponseBadRequest
    @Parameter(name = "startDate", description = "Start date in dd-MM format", example = "01-06")
    @Parameter(name = "endDate", description = "End date in dd-MM format", example = "30-06")
    public List<UserBirthdayDto> findBirthdays(@AuthenticationPrincipal Jwt jwt, @RequestParam @Parameter(hidden = true) String startDate,
                    @RequestParam @Parameter(hidden = true) String endDate) {
        return userService.findBirthdaysBetween(jwt.getSubject(), startDate, endDate);
    }
}
