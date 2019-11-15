package uedson.reis.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uedson.reis.models.DateRange;
import uedson.reis.models.entities.Reservation;
import uedson.reis.models.entities.User;
import uedson.reis.repositories.ReservationDAO;
import uedson.reis.utils.DateUtil;

@Service
public class ReservationService {
	
	private static final String INITIAL_MSG = "We can't be booking your reservation";
	private static final String FINAL_MSG = "and the check-in & check-out time is 12:00 AM.";
	
	@Autowired
	private ReservationDAO dao;
	
	public ReservationService(ReservationDAO dao) {
		this.dao = dao;
	}
	
	public List<DateRange> availability(Date initialDate, Date finalDate) throws Exception {
		if (initialDate == null) {
			Calendar cid = new GregorianCalendar();
			cid.add(Calendar.DATE, 1);
			initialDate = cid.getTime();
		}
		
		if (finalDate == null) {
			Calendar cfd = new GregorianCalendar();
			cfd.setTime(initialDate);
			cfd.add(Calendar.DATE, 30);
			finalDate = cfd.getTime();
		}

		this.checkInvalidDates(initialDate, finalDate);
		
		initialDate = this.adjustToCheckIn(initialDate);
		finalDate = this.adjustToCheckOut(finalDate);
		
		final List<DateRange> ranges = new ArrayList<>();
		
		Date point = initialDate;

		for (Reservation reservation : this.dao.findByRange(initialDate, finalDate)) {
			this.addRange(ranges, point, reservation.getArrival());
			point = reservation.getDeparture();
		}
		
		this.addRange(ranges, point, finalDate);
		
		return ranges;
	}
	
	private void addRange(List<DateRange> ranges, Date first, Date last) {
		long difference = last.getTime() - first.getTime();
		
		System.out.println("Diff in days: "+TimeUnit.MILLISECONDS.toDays(difference));
		
		if (difference > 0) {
			DateRange range = new DateRange();
			range.setInitialDate(first);
			range.setFinalDate(last);
			ranges.add(range);
		}
	}

	public Long booking(String email, String name, Date arrival, Date departure) throws Exception {
        
		User user = new User();
		user.setEmail(email);
		user.setName(name);
		
		this.checkInvalidDates(arrival, departure);

		Date checkIn = this.adjustToCheckIn(arrival);
		Date checkOut = this.adjustToCheckOut(departure);
		
		if (this.isTimeExceedingMax(checkIn, departure)) {
			throw new Exception(INITIAL_MSG+ ". The campsite can be reserved for max 3 days " +FINAL_MSG);
		}
		
		if (this.isBookingTimeInvalid(checkIn)) {
			throw new Exception(INITIAL_MSG
					+ ". The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance "
					+ FINAL_MSG);
		}

		Reservation newReservation = new Reservation();
		newReservation.setUser(user);
		newReservation.setArrival(checkIn);
		newReservation.setDeparture(checkOut);
		
		Reservation saved = this.dao.save(newReservation);
		
		if (saved == null) {
			throw new Exception(INITIAL_MSG+ ", because it already exists or the period is not available!");
		}
		
		return saved.getId();
	}
	
	public void changing(Long id, Date arrival, Date departure) throws Exception {
		
		this.checkInvalidDates(arrival, departure);

		Date checkIn = this.adjustToCheckIn(arrival);
		Date checkOut = this.adjustToCheckOut(departure);
		
		if (this.isBookingTimeInvalid(checkIn)) {
			throw new Exception(INITIAL_MSG
					+ ". The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance "
					+ FINAL_MSG);
		}
		
		if (this.isTimeExceedingMax(checkIn, departure)) {
			throw new Exception(INITIAL_MSG+ ". The campsite can be reserved for max 3 days " +FINAL_MSG);
		}
		
		Reservation newReservation = new Reservation();
		newReservation.setId(id);
		newReservation.setArrival(checkIn);
		newReservation.setDeparture(checkOut);
		
		Reservation updated = this.dao.update(newReservation);

		if (updated == null) {
			throw new Exception(INITIAL_MSG+ ", because it doesn't exist!");
		}
	}
	
	public void cancel(Long id) throws Exception {
		if (!this.dao.delete(id)) {
			throw new Exception("We can't delete your reservation, because it doesn't exist!");
		}
	}
	
	private void checkInvalidDates(Date arrival, Date departure) throws Exception {
		if (arrival.compareTo(departure) > 0) {
			throw new Exception(INITIAL_MSG+ ". The initial date must be after the final date.");
		}
	}
	
	private Date adjustToCheckIn(Date arrival) {
		Calendar checkIn = new GregorianCalendar();
		checkIn.setTime(arrival);
		
		if (checkIn.get(Calendar.HOUR_OF_DAY) < 12) {
			checkIn.add(Calendar.DATE, -1);
		}
		DateUtil.setDefaultHMS(checkIn);
		
		return checkIn.getTime();
	}
	
	private Date adjustToCheckOut(Date departure) {
		Calendar checkOut = new GregorianCalendar();
		checkOut.setTime(departure);
		
		if (checkOut.get(Calendar.HOUR_OF_DAY) > 12) {
			checkOut.add(Calendar.DATE, 1);
		}
		DateUtil.setDefaultHMS(checkOut);
		
		return checkOut.getTime();
	}
	
	private boolean isTimeExceedingMax(Date arrival, Date departure) {
		Calendar checkOut = new GregorianCalendar();
		checkOut.setTime(arrival);
		checkOut.add(Calendar.DATE, 3);
		
		Calendar calDeparture = new GregorianCalendar();
		calDeparture.setTime(departure);
		
		return checkOut.before(calDeparture);
	}
	
	private boolean isBookingTimeInvalid(Date arrival) {
		Date today = new Date();
		
		long diff = arrival.getTime() - today.getTime();

		long diffHours = TimeUnit.MILLISECONDS.toHours(diff);
		
		if (diffHours < 24) return true;
		
		if (diffHours > (24 * 30)) return true;
		
		return false;
	}

}