package xyz.pwmw.mynlife.model.into_hobby_cost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.pwmw.mynlife.model.users.Users;

import java.util.List;


public interface IntoHobbyCostRepository extends JpaRepository<IntoHobbyCost, IntoHobbyCostId> {

    @Query(
            value = "select * from into_hobby_cost where hobby_id = :id",
            nativeQuery = true
    )
    List<IntoHobbyCost> findAllByHobbyId(long id);

}
