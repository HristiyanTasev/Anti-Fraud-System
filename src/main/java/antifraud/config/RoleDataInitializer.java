package antifraud.config;

import antifraud.model.user.AvailableUserRoles;
import antifraud.model.user.UserRoleEntity;
import antifraud.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private final UserRoleRepository roleRepository;

    public RoleDataInitializer(UserRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (AvailableUserRoles role : AvailableUserRoles.values()) {
            roleRepository.findByRole(role)
                    .orElseGet(() -> {
                        UserRoleEntity newRole = new UserRoleEntity(role);
                        return roleRepository.save(newRole);
                    });
        }
    }
}

