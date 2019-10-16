package uedson.reis.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import uedson.reis.models.entities.Reservation;
import uedson.reis.models.entities.User;
import uedson.reis.repositories.ReservationRepository;
import uedson.reis.repositories.UserRepository;
import uedson.reis.utils.DateUtil;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.CONCURRENT)
public class ReservationServiceTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@BeforeAll
	public void prepareDatabase() {
		User user = new User();
		user.setEmail("uedsonreis@gmail.com");
		user.setName("Uedson Reis");
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());

		calendar.add(Calendar.DATE, 2);
		DateUtil.setDefaultHMS(calendar);
		Date arrival = calendar.getTime();
		
		calendar.add(Calendar.DATE, 2);
		Date departure = calendar.getTime();
		
		Reservation reservation = new Reservation();
		reservation.setUser(user);
		reservation.setArrival(arrival);
		reservation.setDeparture(departure);
		
		this.userRepository.save(user);
		this.reservationRepository.save(reservation);
	}
	
	@AfterAll
	public void clearDatabase() {
		this.reservationRepository.deleteAll();
		this.userRepository.deleteAll();
	}
	
	@RepeatedTest(30)
	public void testBookingBusyPeriod() {
		
		Calendar calendar = new GregorianCalendar();
		
		for (int i=1; i <= 3; i++) {
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, i);
			DateUtil.setDefaultHMS(calendar);
			
			String arrival = DateUtil.FORMATTER.format(calendar.getTime());
			
			calendar.add(Calendar.DATE, 2);
			String departure = DateUtil.FORMATTER.format(calendar.getTime());
			
			String url = "/booking?email=edwardreyes@anymail.com&name=Edward Reyes&arrival="+arrival+"&departure="+departure;
			
			try {
				this.mockMvc.perform(get(url))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("success").value(false));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@RepeatedTest(30)
	public void testBookingChangeCancel() {
		System.out.println("Start test 1 booking...");

		User user = new User();
		user.setEmail("hectorreyes@anymail.com");
		user.setName("Hector Reyes");
		
		for (int i=1; i <= 3; i++) {
			Reservation reservation = new Reservation();
			reservation.setUser(user);
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 8*i);
			
			reservation.setArrival(calendar.getTime());
			
			calendar.add(Calendar.DATE, 1);
			reservation.setDeparture(calendar.getTime());
			
			this.testBookingChangeCancel(reservation);
		}
		
		System.out.println("Test 1 booking has finished.");
	}

	@Test
	public void testAvailability() {
		System.out.println("Start test 2 availability...");
		
		try {
			this.mockMvc.perform(get("/availability"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("success").value(true));
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Test 2 availability has finished.");
	}
	
	private void testBookingChangeCancel(Reservation reservation) {
		try {
			Long id = this.testBooking(reservation);
			
			if (id != null) {
				System.out.println("Test 1 booked...");
				
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(reservation.getDeparture());
				calendar.add(Calendar.DATE, 2);
				
				this.testChange(id, reservation.getArrival(), calendar.getTime());
				System.out.println("Test 1 changed...");
				
				this.testCancel(id);
				System.out.println("Test 1 canceled...");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Long testBooking(Reservation reservation) throws Exception {
		String arrival = DateUtil.FORMATTER.format(reservation.getArrival());
		String departure = DateUtil.FORMATTER.format(reservation.getDeparture());
		
		String email = reservation.getUser().getEmail();
		String name = reservation.getUser().getName();
		
		String url = "/booking?email="+email+"&name="+name+"&arrival="+arrival+"&departure="+departure;
		
		ResultActions result = this.mockMvc.perform(get(url))
			.andDo(print())
			.andExpect(status().isOk());
		
		Object data = new JSONObject(result.andReturn().getResponse().getContentAsString()).get("data");
		
		try {
			return Long.parseLong(data+"");

		} catch(NumberFormatException nfe) {
			return null;
		}
	}
	
	private void testChange(long id, Date arrival, Date departure) throws Exception {
		String arrivalTxt = DateUtil.FORMATTER.format(arrival);
		String departureTxt = DateUtil.FORMATTER.format(departure);
		
		String url = "/change?id="+id+"&&arrival="+arrivalTxt+"&departure="+departureTxt;
		
		this.mockMvc.perform(get(url))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	private void testCancel(long id) throws Exception {
		this.mockMvc.perform(get("/cancel?id="+id))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true));
	}

}