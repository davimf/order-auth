package br.davimf.food.example.repository;

import br.davimf.food.example.entity.User;
import br.davimf.food.example.entity.security.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findFirstByRole(Role role);

  Optional<User> findFirstByUserName(String userName);
}
