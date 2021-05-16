package slotchecking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import util.Constants;
import util.CowinResources;

public class FindSlot {

	public static String GetAvailableSlotsCalendarByPinResponse(Integer pincode, String date) throws IOException {

		String finalUrl = CowinResources.getProperty(Constants.BASE_URL)
				.concat(CowinResources.getProperty(Constants.BASE_PATH))
				.concat(CowinResources.getProperty(Constants.API_APOINT_CAL_BY_PIN));
		finalUrl = finalUrl.replace("##pincode##", pincode.toString());
		finalUrl = finalUrl.replace("##date##", date);

		try {
			URL url = new URL(finalUrl);
			return GetEndpointResponse(url, pincode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String GetAvailableSlotsCalendarByDistrictResponse(Integer districtid, String date) {

		String finalUrlString = CowinResources.getProperty(Constants.BASE_URL)
				.concat(CowinResources.getProperty(Constants.BASE_PATH))
				.concat(CowinResources.getProperty(Constants.API_APOINT_CAL_BY_DISTRICT));
		finalUrlString = finalUrlString.replace("##districtid##", districtid.toString());
		finalUrlString = finalUrlString.replace("##date##", date);

		try {
			URL url = new URL(finalUrlString);
			return GetEndpointResponse(url, districtid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			URL url = new URL(finalUrlString);
			return GetEndpointResponse(url, districtid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<SlotInfo> getAllAvailableSlots(String stringBackendResponse, int placeId, User user, boolean isDistrict) {

		JSONObject response = new JSONObject(stringBackendResponse);
		List<SlotInfo> slotInfoList = new ArrayList<>();

		// get all centers
		JSONArray centres = response.getJSONArray("centers");

		for (int i = 0; i < centres.length(); i++) {
			JSONObject centre = centres.getJSONObject(i);

			// get all sessions
			JSONArray sessions = centre.getJSONArray("sessions");

			for (int j = 0; j < sessions.length(); j++) {
				JSONObject session = sessions.getJSONObject(j);
				
				if(isMinAgeCriteriaSatisfied(user, session) && isSlotCapacityCriteriaSatisfied(user, session) &&
						isPreferredVaccineAvailableInSession(user, session)) {
					slotInfoList.add(createSlotInfo(placeId, centre, session, user, isDistrict));
				}
			}
		}
		return slotInfoList;
	}

	private static boolean isMinAgeCriteriaSatisfied(User user, JSONObject sessionJson) {
		return user.getMinAge() == sessionJson.getInt("min_age_limit");
	}

	private static boolean isSlotCapacityCriteriaSatisfied(User user, JSONObject sessionJson) {
		boolean isCriteriaSatisfied = false;
		if (user.isDose1Preferred()) {
			isCriteriaSatisfied = isCriteriaSatisfied
					|| sessionJson.getInt("available_capacity_dose1") > user.getMinCapacity();
		}
		if (user.isDose2Preferred()) {
			isCriteriaSatisfied = isCriteriaSatisfied
					|| sessionJson.getInt("available_capacity_dose2") > user.getMinCapacity();
		}

		return isCriteriaSatisfied;
	}
	
	private static boolean isPreferredVaccineAvailableInSession(User user, JSONObject sessionJson) {
		return (user.isAnyVaccinePreferred() && StringUtils.isNotBlank(user.getPreferredVaccine())
				&& sessionJson.getString("vaccine") == user.getPreferredVaccine()) || !user.isAnyVaccinePreferred();
	}

	private static SlotInfo createSlotInfo(int placeId, JSONObject centre, JSONObject session, User user, boolean isDistrict) {
		SlotInfo slotinfo = new SlotInfo();
		slotinfo.setCapacity(session.getInt("available_capacity"));
		slotinfo.setCenter(centre.getString("name"));
		if(isDistrict) {
			slotinfo.setDistrictId(placeId);
			slotinfo.setPincode(centre.getInt("pincode"));
		} else {
			slotinfo.setDistrictId(user.getDistrictId());
			slotinfo.setPincode(placeId);
		}

		slotinfo.setDistrictName(user.getDistrictName());
		slotinfo.setVaccine(session.getString("vaccine"));
		slotinfo.setMinAge(session.getInt("min_age_limit"));
		slotinfo.setDose1Preferred(user.isDose1Preferred());
		slotinfo.setDose2Preferred(user.isDose2Preferred());
		slotinfo.setDose1Capacity(session.getInt("available_capacity_dose1"));
		slotinfo.setDose2Capacity(session.getInt("available_capacity_dose2"));
		slotinfo.setDate(session.getString("date"));
		return slotinfo;
	}

	private static String GetEndpointResponse(URL url, int placeId) throws IOException, ProtocolException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36");
		con.setRequestProperty("accept", "application/json");
		con.setInstanceFollowRedirects(true);


		if (con.getResponseCode() == 200) {
			InputStream responseStream = con.getInputStream();
			InputStreamReader inReader = new InputStreamReader(responseStream);
			BufferedReader reader = new BufferedReader(inReader);
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} else {
			System.out.println(url.getPath() + " returned " + con.getResponseCode() + "place ID: " + placeId);
		}
		return null;
	}
}
