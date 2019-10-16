package uedson.reis.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uedson.reis.models.DateRange;
import uedson.reis.models.Result;
import uedson.reis.services.ReservationService;

@RestController
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@RequestMapping("/availability")
	public Result availability(
			@RequestParam(value="initialDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date initialDate,
    		@RequestParam(value="finalDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date finalDate) {

		final Result result = new Result();
		
		try {
			List<DateRange> ranges = this.reservationService.availability(initialDate, finalDate);
			result.setData(ranges);
			result.setMessage("Based on the range, these are the available dates to stay in the campsite.");
			
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		
		return result;
	}

	@RequestMapping("/booking")
    public Result booking(
    		@RequestParam(value="email") String email,
    		@RequestParam(value="name") String name,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) {
		
		final Result result = new Result();
		
		try {
			Long id = this.reservationService.booking(email, name, arrival, departure);
			result.setData(id);
			result.setMessage("The reservation was booked successfully.");
			
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		
		return result;
    }
	
	@RequestMapping("/change")
    public Result changing(
    		@RequestParam(value="id") Long id,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) {
		
		final Result result = new Result();
		
		try {
			this.reservationService.changing(id, arrival, departure);
			result.setMessage("The reservation was changed successfully.");
			
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		
		return result;
    }
	
	@RequestMapping("/cancel")
    public Result cancel(
    		@RequestParam(value="id") Long id) {
		
		final Result result = new Result();
		
		try {
			this.reservationService.cancel(id);
			result.setMessage("The reservation was canceled successfully.");
			
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		
		return result;
    }

}