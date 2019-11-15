package uedson.reis.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uedson.reis.models.DateRange;
import uedson.reis.models.Result;
import uedson.reis.services.ReservationService;

@ControllerAdvice
@RestController
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@GetMapping("/availability")
	public Result availability(
			@RequestParam(value="initialDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date initialDate,
    		@RequestParam(value="finalDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date finalDate) throws Exception {

		final Result result = new Result();
		
		List<DateRange> ranges = this.reservationService.availability(initialDate, finalDate);
		result.setData(ranges);
		result.setMessage("Based on the range, these are the available dates to stay in the campsite.");
		result.setSuccess(true);
			
		return result;
	}

	@PostMapping("/booking")
    public Result booking(
    		@RequestParam(value="email") String email,
    		@RequestParam(value="name") String name,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) throws Exception {
		
		final Result result = new Result();
		
		Long id = this.reservationService.booking(email, name, arrival, departure);
		result.setData(id);
		result.setMessage("The reservation was booked successfully.");
		result.setSuccess(true);
			
		return result;
    }
	
	@RequestMapping("/change/{id}/reservation")
    public Result changing(
    		@PathVariable(value="id") String id,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) 
    				throws Exception {
		
		final Result result = new Result();
		
		this.reservationService.changing(Long.valueOf(id), arrival, departure);
		result.setMessage("The reservation was changed successfully.");
		result.setSuccess(true);
				
		return result;
    }
	
	@DeleteMapping("/cancel/{id}/reservation")
    public Result cancel(
    		@PathVariable(value="id") String id) throws Exception {
		
		final Result result = new Result();
		
		this.reservationService.cancel(Long.valueOf(id));
		result.setMessage("The reservation was canceled successfully.");
		result.setSuccess(true);
		
		return result;
    }

}