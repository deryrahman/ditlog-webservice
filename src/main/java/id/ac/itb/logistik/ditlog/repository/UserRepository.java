package id.ac.itb.logistik.ditlog.repository;

import id.ac.itb.logistik.ditlog.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  @Query(value = "SELECT * FROM SILOG_USER WHERE USER_NAME = ?1 AND USER_PASSWORD = ?2", nativeQuery = true)
  User findUserByUsernamePassword(String username, String password);
  @Query(value = "SELECT * FROM SILOG_USER WHERE USER_NAME = ?1", nativeQuery = true)
  User findUserByUsername(String username);
}

