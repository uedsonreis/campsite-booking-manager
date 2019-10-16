package uedson.reis.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uedson.reis.models.entities.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

}