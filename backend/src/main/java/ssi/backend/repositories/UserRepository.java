package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByUserType(String userType);

    User findByEmail(String email);

    @Query("SELECT u.email FROM User u")
    List<String> findAllEmail();


    User findByIdAndEmail(Integer id, String email);
}
