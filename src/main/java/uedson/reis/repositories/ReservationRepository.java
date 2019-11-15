package uedson.reis.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uedson.reis.models.entities.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

	@Query("SELECT r FROM Reservation r "
		       + "WHERE (:initialDate <= r.arrival AND r.departure <= :finalDate) "
		       + "OR ((r.arrival <= :initialDate AND :initialDate < r.departure) "
		       + "OR (r.arrival < :finalDate AND :finalDate <= r.departure))"
		       + "order by r.arrival ")
	List<Reservation> find(Date initialDate, Date finalDate);

}