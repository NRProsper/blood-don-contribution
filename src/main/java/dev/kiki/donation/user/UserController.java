package dev.kiki.donation.user;

import dev.kiki.donation.user.dto.RegisterResponseDto;
import dev.kiki.donation.user.dto.UpdateDto;
import dev.kiki.donation.user.dto.UserInfo;
import dev.kiki.donation.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "auth")
@Tag(name = "User Controller")
public class UserController {
    private final UserService userService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers() {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        byte[] csvData = userService.exportUsersToCSV();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users_" + timestamp + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        int realPage = page - 1;
        Pageable pageable = PageRequest.of(realPage, size);

        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RegisterResponseDto> updateUser(
            @PathVariable(name = "userId") Long userId,
            @RequestBody UpdateDto updateDto

    ) {
        var updatedUser = userService.updateUser(userId, updateDto);
        UserInfo userInfo = new UserInfo(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getUsername(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRole()
        );
        RegisterResponseDto response = new RegisterResponseDto(
                "User updated successfully",
                userInfo
        );
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/userId")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
