package uedson.reis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uedson.reis.models.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findByEmail(String email);
	User findById(long id);

}