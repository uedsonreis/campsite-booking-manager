package uedson.reis.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uedson.reis.models.entities.Reservation;
import uedson.reis.models.entities.User;

@Service
public class ReservationDAO {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Transactional
	public Reservation save(Reservation reservation) {
		
		if (reservation.getId() != null) return null;
		
		List<Reservation> ranges = this.findByRange(reservation.getArrival(), reservation.getDeparture());
		
		if (ranges != null && ranges.size() > 0) return null;

		User userSaved = this.save(reservation.getUser());
		reservation.setUser(userSaved);
		
		return this.reservationRepository.save(reservation);
	}
	
	@Transactional
	public Reservation update(Reservation reservation) {
		if (reservation.getId() == null) return null;
		
		Optional<Reservation> op = this.reservationRepository.findById(reservation.getId());
		if (op.isPresent()) {
			Reservation saved = op.get();
			saved.setArrival(reservation.getArrival());
			saved.setDeparture(reservation.getDeparture());
			return this.reservationRepository.save(saved);
		}
		
		return null;
	}
	
	private User save(User user) {
		if (user.getId() == null) {
			
			List<User> users = this.userRepository.findByEmail(user.getEmail());
			if (users == null || users.size() <= 0) {
				return this.userRepository.save(user);
			} else {
				return users.get(0);
			}
		}
		return user;
	}
	
	public List<Reservation> findByRange(Date initialDate, Date finalDate) {

		String query = "SELECT r FROM Reservation r WHERE (?1 <= r.arrival AND r.departure <= ?2) OR "
				+ "((r.arrival <= ?1 AND ?1 < r.departure) OR (r.arrival < ?2 AND ?2 <= r.departure))"
				+ "order by r.arrival";
		
		TypedQuery<Reservation> typedQuery = this.entityManager.createQuery(query, Reservation.class);
		typedQuery.setParameter(1, initialDate);
		typedQuery.setParameter(2, finalDate);
		
		return typedQuery.getResultList();
	}
	
	@Transactional
	public boolean delete(long id) {
		Optional<Reservation> op = this.reservationRepository.findById(id);
		if (!op.isPresent()) return false;
			
		this.reservationRepository.deleteById(id);
		return true;
	}

}