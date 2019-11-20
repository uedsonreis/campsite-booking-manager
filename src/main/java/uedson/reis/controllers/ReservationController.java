package uedson.reis.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@GetMapping("/availability")
	public ResponseEntity<Result> availability(
			@RequestParam(value="initialDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date initialDate,
    		@RequestParam(value="finalDate", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date finalDate) throws Exception {

		final Result result = new Result();
		
		try {
			List<DateRange> ranges = this.reservationService.availability(initialDate, finalDate);
			result.setData(ranges);
			result.setMessage("Based on the range, these are the available dates to stay in the campsite.");
			
			return new ResponseEntity<>(result, HttpStatus.OK);
			
		} catch (Exception e) {
			result.setMessage(e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/booking")
    public ResponseEntity<Long> booking(
    		@RequestParam(value="email") String email,
    		@RequestParam(value="name") String name,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) throws Exception {
		
		try {
			Long id = this.reservationService.booking(email, name, arrival, departure);
			return new ResponseEntity<>(id, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<Long>(HttpStatus.CONFLICT);
		}
    }
	
	@RequestMapping("/change/{id}/reservation")
    public ResponseEntity<String> changing(
    		@PathVariable(value="id") String id,
    		@RequestParam(value="arrival") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date arrival,
    		@RequestParam(value="departure") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date departure) 
    				throws Exception {
		
		try {
			this.reservationService.changing(Long.valueOf(id), arrival, departure);
			return new ResponseEntity<>(HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
	
	@DeleteMapping("/cancel/{id}/reservation")
    public ResponseEntity<String> cancel(
    		@PathVariable(value="id") String id) throws Exception {
		
		try {
			this.reservationService.cancel(Long.valueOf(id));
			return new ResponseEntity<>(HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }

}