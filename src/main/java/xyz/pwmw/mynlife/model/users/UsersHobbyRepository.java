package xyz.pwmw.mynlife.model.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersHobbyRepository extends JpaRepository<UsersHobby, UsersHobbyId> {
    UsersHobby findByUsersHobbyId(UsersHobbyId id);
}
