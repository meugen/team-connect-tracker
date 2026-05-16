package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseBadRequest;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public PageDto<UserDto> findFiltered(@RequestParam Map<String, String> params) {
        return userService.findFiltered(params);
    }
}
