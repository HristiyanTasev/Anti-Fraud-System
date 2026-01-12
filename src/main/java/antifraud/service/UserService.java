package antifraud.service;

import antifraud.config.SecurityConfig;
import antifraud.exception.role.RoleAlreadyProvidedException;
import antifraud.exception.role.RoleChangeNotPermittedException;
import antifraud.exception.user.UserExistsException;
import antifraud.model.user.AvailableUserRoles;
import antifraud.model.user.UserEntity;
import antifraud.model.user.UserRoleEntity;
import antifraud.model.user.dto.*;
import antifraud.repository.UserRepository;
import antifraud.repository.UserRoleRepository;
import antifraud.util.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityConfig securityConfig;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.securityConfig = securityConfig;
    }

    public UserDtoOut registerUser(UserDtoIn userDtoIn) {

        if (userRepository.existsByUsername(userDtoIn.getUsername())) {
            throw new UserExistsException("User with this username already exists");
        }

        boolean isFirstUser = this.userRepository.count() == 0;

        UserRoleEntity userRole = userRoleRepository.findByRole(
                isFirstUser
                        ? AvailableUserRoles.ADMINISTRATOR
                        : AvailableUserRoles.MERCHANT
        ).orElseThrow(() -> new IllegalStateException("Role not seeded"));

        UserEntity newUser = new UserEntity(
                userDtoIn.getName(),
                userDtoIn.getUsername(),
                securityConfig.passwordEncoder().encode(userDtoIn.getPassword()),
                userRole,
                isFirstUser // sets Enabled to true if it's first user (administrator)
        );

        this.userRepository.save(newUser);
        return new UserDtoOut(newUser.getId(), newUser.getName(), newUser.getUsername(), userRole.getRole().name());

    }

    public List<UserDtoOut> getAllUsers() {
        List<UserEntity> allUsers = this.userRepository.findAllByOrderByIdAsc();

        return UserMapper.userListToDtoList(allUsers);
    }

    public UserStatusDto deleteUser(String username) {
        Optional<UserEntity> byUsername = this.userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            this.userRepository.delete(byUsername.get());
            return new UserStatusDto(username, "Deleted successfully!");
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public UserDtoOut updateRole(RoleChangeDto roleChangeDto) throws RoleNotFoundException {
        UserEntity user = this.userRepository.findByUsername(roleChangeDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(roleChangeDto.getUsername()));

        boolean isRolePermitted = roleChangeDto.getRole().equals(AvailableUserRoles.MERCHANT.name()) ||
                                  roleChangeDto.getRole().equals(AvailableUserRoles.SUPPORT.name());

        if (!isRolePermitted) {
            throw new RoleChangeNotPermittedException("Unavailable role.");
        }

        if (user.getRole().getRole().name().equals(roleChangeDto.getRole())) {
            throw new RoleAlreadyProvidedException("User already has that role.");
        }

        user.setRole(this.userRoleRepository
                .findByRole(AvailableUserRoles.valueOf(roleChangeDto.getRole()))
                .orElseThrow(() -> new RoleNotFoundException("Role not found.")));

        userRepository.save(user);

        return new UserDtoOut(user.getId(), user.getName(), user.getUsername(), user.getRole().getRole().name());
    }

    public Map<String, String> updateAccess(AccessChangeDto accessChangeDto) {
        UserEntity user = this.userRepository.findByUsername(accessChangeDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(accessChangeDto.getUsername()));

        boolean isEnabled = accessChangeDto.getOperation() == AccessOperation.UNLOCK;

        user.setEnabled(isEnabled);
        userRepository.save(user);

        String statusMessage = "User " + user.getUsername() + " " + (isEnabled ? "unlocked" : "locked") + "!";

        return Map.of("status", statusMessage);
    }

}
