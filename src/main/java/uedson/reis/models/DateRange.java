package uedson.reis.models;

import java.util.Date;

import uedson.reis.utils.DateUtil;

public class DateRange {
	
	private Date initialDate;
	private Date finalDate;
	
	public DateRange() {}

	public DateRange(Date initialDate, Date finalDate) {
		this.initialDate = initialDate;
		this.finalDate = finalDate;
	}
	
	public String getInitialDate() {
		return DateUtil.FORMATTER.format(this.initialDate);
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}
	
	public String getFinalDate() {
		return DateUtil.FORMATTER.format(this.finalDate);
	}
	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}
	
	@Override
	public String toString() {
		return "It is from " + this.getInitialDate() + " to " + this.getFinalDate() + ".";
	}
}
