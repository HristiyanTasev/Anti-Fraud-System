package antifraud.repository;

import antifraud.model.user.AvailableUserRoles;
import antifraud.model.user.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByRole(AvailableUserRoles role);
}
