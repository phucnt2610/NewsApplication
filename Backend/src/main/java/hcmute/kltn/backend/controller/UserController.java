package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.request.UpdatePassRequest;
import hcmute.kltn.backend.dto.UserDTO;
import hcmute.kltn.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get-user")
    public ResponseEntity<UserDTO> getUser(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/get-my-infor")
    public ResponseEntity<UserDTO> getMyInfor() {
        return ResponseEntity.ok(userService.getMyInfor());
    }

    @PostMapping("/update-user-infor")
    public ResponseEntity<UserDTO> updateUserInfor(
            @RequestParam("userId") String userId,
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserInfor(userId, userDTO));
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PostMapping("/update-user-avatar")
    public ResponseEntity<UserDTO> updateUserAvatar(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateUserAvatar(file));
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UpdatePassRequest updatePassRequest) {
        return ResponseEntity.ok(userService.updatePassword(updatePassRequest));
    }

}
