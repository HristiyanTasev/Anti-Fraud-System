package antifraud.controller;

import antifraud.model.user.dto.*;
import antifraud.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserDtoOut> registerUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.registerUser(userDtoIn));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDtoOut>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserStatusDto> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(this.userService.deleteUser(username));
    }

    @PutMapping("/role")
    public ResponseEntity<UserDtoOut> updateRole(@Valid @RequestBody RoleChangeDto roleChangeDto) throws RoleNotFoundException {
        return ResponseEntity.ok(this.userService.updateRole(roleChangeDto));
    }

    @PutMapping("/access")
    public ResponseEntity<Map<String, String>> updateAccess(@Valid @RequestBody AccessChangeDto accessChangeDto) {
        return ResponseEntity.ok(this.userService.updateAccess(accessChangeDto));
    }
}
