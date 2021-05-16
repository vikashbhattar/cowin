package test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import slotchecking.FindSlot;
import slotchecking.SlotInfo;
import slotchecking.User;

public class TestSlotBooking {

	@Test
	public void testResponseParsing() {
		
		//backend response
		String response = "{\r\n" + 
				"  \"centers\": [\r\n" + 
				"    {\r\n" + 
				"      \"center_id\": 603520,\r\n" + 
				"      \"name\": \"Dergaon SHC (COVAXIN)\",\r\n" + 
				"      \"address\": \"Dergaon SHC\",\r\n" + 
				"      \"state_name\": \"Assam\",\r\n" + 
				"      \"district_name\": \"Golaghat\",\r\n" + 
				"      \"block_name\": \"Missamora 	 BPHC\",\r\n" + 
				"      \"pincode\": 785614,\r\n" + 
				"      \"lat\": 26,\r\n" + 
				"      \"long\": 93,\r\n" + 
				"      \"from\": \"09:00:00\",\r\n" + 
				"      \"to\": \"17:00:00\",\r\n" + 
				"      \"fee_type\": \"Free\",\r\n" + 
				"      \"sessions\": [\r\n" + 
				"        {\r\n" + 
				"          \"session_id\": \"737ad0da-bac8-4ce9-8412-a1b7addb453b\",\r\n" + 
				"          \"date\": \"06-05-2021\",\r\n" + 
				"          \"available_capacity\": 1,\r\n" + 
				"          \"min_age_limit\": 45,\r\n" + 
				"          \"vaccine\": \"COVAXIN\",\r\n" + 
				"          \"available_capacity_dose1\": 1,\r\n"+ 
				"          \"available_capacity_dose2\": 0,\r\n"+ 
				"          \"slots\": [\r\n" + 
				"            \"09:00AM-11:00AM\",\r\n" + 
				"            \"11:00AM-01:00PM\",\r\n" + 
				"            \"01:00PM-03:00PM\",\r\n" + 
				"            \"03:00PM-05:00PM\"\r\n" + 
				"          ]\r\n" + 
				"        }\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"center_id\": 576481,\r\n" + 
				"      \"name\": \"Nahardonga Model\",\r\n" + 
				"      \"address\": \"Nahardonga\",\r\n" + 
				"      \"state_name\": \"Assam\",\r\n" + 
				"      \"district_name\": \"Golaghat\",\r\n" + 
				"      \"block_name\": \"Missamora BPHC\",\r\n" + 
				"      \"pincode\": 785614,\r\n" + 
				"      \"lat\": 26,\r\n" + 
				"      \"long\": 94,\r\n" + 
				"      \"from\": \"09:00:00\",\r\n" + 
				"      \"to\": \"15:00:00\",\r\n" + 
				"      \"fee_type\": \"Free\",\r\n" + 
				"      \"sessions\": [\r\n" + 
				"        {\r\n" + 
				"          \"session_id\": \"22a8888a-2934-4122-9e53-703d19296ee0\",\r\n" + 
				"          \"date\": \"07-05-2021\",\r\n" + 
				"          \"available_capacity\": 5,\r\n" + 
				"          \"min_age_limit\": 18,\r\n" + 
				"          \"vaccine\": \"COVAXIN\",\r\n" +
				"          \"available_capacity_dose1\": 5,\r\n"+ 
				"          \"available_capacity_dose2\": 0,\r\n"+ 
				"          \"slots\": [\r\n" + 
				"            \"09:00AM-10:00AM\",\r\n" + 
				"            \"10:00AM-11:00AM\",\r\n" + 
				"            \"11:00AM-12:00PM\",\r\n" + 
				"            \"12:00PM-03:00PM\"\r\n" + 
				"          ]\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"session_id\": \"22a8888a-2934-4122-9e53-703d19296ee1\",\r\n" + 
				"          \"date\": \"08-05-2021\",\r\n" + 
				"          \"available_capacity\": 1,\r\n" + 
				"          \"min_age_limit\": 18,\r\n" + 
				"          \"vaccine\": \"COVISHIELD\",\r\n" + 
				"          \"available_capacity_dose1\": 0,\r\n"+ 
				"          \"available_capacity_dose2\": 1,\r\n"+ 
				"          \"slots\": [\r\n" + 
				"            \"09:00AM-10:00AM\",\r\n" + 
				"            \"10:00AM-11:00AM\",\r\n" + 
				"            \"11:00AM-12:00PM\",\r\n" + 
				"            \"12:00PM-03:00PM\"\r\n" + 
				"          ]\r\n" + 
				"        }\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		
		User user = new User();
		user.setDistrictId(1);
		user.setDistrictName("districtName");
		user.setMinAge(18);
		user.setMinCapacity(0);
		user.setDose1Preferred(true);

		List<SlotInfo> slotInfoList = FindSlot.getAllAvailableSlots(response, 1, user, true);
		
		assertEquals(1, slotInfoList.size());
		assertEquals("Nahardonga Model", slotInfoList.get(0).getCenter());
		assertEquals("COVAXIN", slotInfoList.get(0).getVaccine());
		assertEquals(5, slotInfoList.get(0).getCapacity());
		assertEquals(5, slotInfoList.get(0).getDose1Capacity());
		assertEquals(0, slotInfoList.get(0).getDose2Capacity());
		
		user.setDose1Preferred(true);
		user.setDose2Preferred(true);
		slotInfoList = FindSlot.getAllAvailableSlots(response, 1, user, true);
		assertEquals("Nahardonga Model", slotInfoList.get(1).getCenter());
		assertEquals("COVISHIELD", slotInfoList.get(1).getVaccine());
		assertEquals(1, slotInfoList.get(1).getCapacity());
		assertEquals(1, slotInfoList.get(1).getDose2Capacity());
		assertEquals(0, slotInfoList.get(1).getDose1Capacity());
		
		user.setMinAge(45);
		slotInfoList = FindSlot.getAllAvailableSlots(response, 1, user, true);
		assertEquals(1, slotInfoList.size());
		assertEquals("Dergaon SHC (COVAXIN)", slotInfoList.get(0).getCenter());
		assertEquals("COVAXIN", slotInfoList.get(0).getVaccine());
		assertEquals(1, slotInfoList.get(0).getCapacity());
		assertEquals(0, slotInfoList.get(0).getDose2Capacity());
		assertEquals(1, slotInfoList.get(0).getDose1Capacity());
		
		
		user.setMinAge(45);
		user.setDose2Preferred(true);
		user.setDose1Preferred(false);
		slotInfoList = FindSlot.getAllAvailableSlots(response, 1, user, true);
		assertEquals(0, slotInfoList.size());
	}
}