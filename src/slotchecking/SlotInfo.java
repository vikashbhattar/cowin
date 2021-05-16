package slotchecking;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SlotInfo {

	private String center;
	private int pincode;
	private String vaccine;
	private Date time = new Date();
	private int districtId;
	private String districtName;
	private int capacity;
	private int dose1Capacity;
	private int dose2Capacity;
	private int minAge;
	private boolean dose1Preferred;
	private boolean dose2Preferred;
	private String date;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isDose1Preferred() {
		return dose1Preferred;
	}

	public void setDose1Preferred(boolean dose1Preferred) {
		this.dose1Preferred = dose1Preferred;
	}

	public boolean isDose2Preferred() {
		return dose2Preferred;
	}

	public void setDose2Preferred(boolean dose2Preferred) {
		this.dose2Preferred = dose2Preferred;
	}

	public int getDose1Capacity() {
		return dose1Capacity;
	}

	public void setDose1Capacity(int dose1Capacity) {
		this.dose1Capacity = dose1Capacity;
	}

	public int getDose2Capacity() {
		return dose2Capacity;
	}

	public void setDose2Capacity(int dose2Capacity) {
		this.dose2Capacity = dose2Capacity;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getPushSentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(time);
	}

	@Override
	public String toString() {
		String message = "Centre: " + this.center + " Pin: " + this.pincode + " minAge: " + this.minAge + " Vaccine: "
				+ this.vaccine + " DATE:" + this.date;
		if (dose1Preferred) {
			message = message + " Dose1Capacity : " + this.dose1Capacity;
		}

		if (dose2Preferred) {
			message = message + " Dose2Capacity : " + this.dose2Capacity;
		}
		message = message + "\n";
		return message;
	}
}