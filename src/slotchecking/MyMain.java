package slotchecking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import util.Constants;
import util.CowinResources;

public class MyMain {

	public static void main(String[] args) throws IOException {

		List<User> users = getAllUserDetails(args[0]);

		//serverStartContinuityNotification("Server is alive!", users);

		// get unique districts
		Map<Integer, List<User>> uniqueDistrictIdsMap = getDistrictUsersMap(users);

		int periodicNotificationIntervalInSeconds = CowinResources
				.getIntProperty(Constants.SERVER_LIVE_CHECK_INTERVAL_IN_SEC);
		long startTimeInSeconds = System.currentTimeMillis() / 1000;
		long currentTimeInSeconds;

		long sleepTimeinSec = deriveSleepTimePerAPICall(uniqueDistrictIdsMap);

		System.out.println("\nThere are " + uniqueDistrictIdsMap.size() + " unique districts. "
				+ "System will be sleeping for total " + sleepTimeinSec + " seconds every loop.");

		do {
			try {
				currentTimeInSeconds = System.currentTimeMillis() / 1000;
				if (currentTimeInSeconds > startTimeInSeconds + periodicNotificationIntervalInSeconds) {
					printUserPushStats(users);

					// refresh list of users
					users = getAllUserDetails(args[0]);
					serverStartContinuityNotification("Server is alive!", users);
					System.out.println(CowinResources.getProperty(Constants.SERVER_LIVE_CHECK_MESSAGE));
					startTimeInSeconds = currentTimeInSeconds;
				}

				System.out.println("\n\nChecking Slots at:" + new Date());

				for (Integer key : uniqueDistrictIdsMap.keySet()) {
					Thread.sleep(2 * 1000);

					if (uniqueDistrictIdsMap.get(key).size() > 1) {
						// call district api as more than one looking for that district
						// just to reduce network calls
						System.out.println("Checking slots for district Id: " + key);
						checkSlotsAndNotify(key, users, true);
					} else {
						// instead call pin api, to reduce network load
						System.out.println(
								"Checking slots for pincode: " + uniqueDistrictIdsMap.get(key).get(0).getPin());
						checkSlotsAndNotify(uniqueDistrictIdsMap.get(key).get(0).getPin(), users, false);
					}
				}

				Thread.sleep(sleepTimeinSec * 1000 - 2 * uniqueDistrictIdsMap.size() * 1000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	public static List<Sloka> getSlokas() {
		List<Sloka> slokas = new ArrayList<Sloka>();
		try {
			InputStream stream;
			ClassLoader cl = CowinResources.class.getClassLoader();
			stream = cl.getResourceAsStream("slokas.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

			CsvMapper csvMapper = new CsvMapper();
			CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

			ObjectReader oReader = csvMapper.reader(Sloka.class).with(csvSchema);

			MappingIterator<Sloka> mi = oReader.readValues(br);

			while (mi.hasNext()) {
				Sloka slok = mi.next();

				slokas.add(slok);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return slokas;
	}

	public static List<User> getAllUserDetails(String filePath) {
		List<User> users = new ArrayList<User>();
		try {
			InputStream stream;

			if (StringUtils.isNotBlank(filePath)) {
				stream = new FileInputStream(new File(filePath));
			} else {
				ClassLoader cl = CowinResources.class.getClassLoader();
				stream = cl.getResourceAsStream(CowinResources.getProperty(Constants.USER_DATA_FILE_NAME));
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

			CsvMapper csvMapper = new CsvMapper();
			CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

			ObjectReader oReader = csvMapper.reader(User.class).with(csvSchema);

			MappingIterator<User> mi = oReader.readValues(br);

			int maxNoOfTimesSamePushSend = CowinResources.getIntProperty(Constants.MAX_TIMES_SAME_PUSH_SEND_COUNT);

			while (mi.hasNext()) {
				User user = mi.next();
				if (StringUtils.isNotBlank(user.getName())) {
					user.setMaxNoOfTimesSamePushSend(maxNoOfTimesSamePushSend);
					users.add(user);
					System.out.println("USER DETAILS: " + user.toString());
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return users;
	}

	private static long deriveSleepTimePerAPICall(Map<Integer, List<User>> uniqueDistrictIdsMap) {
		long sleepTimeinSec = (long) Math.ceil(
				60 / (CowinResources.getIntProperty(Constants.MAX_RATE_PER_MINUTE) / uniqueDistrictIdsMap.size()));
		sleepTimeinSec = sleepTimeinSec < CowinResources.getIntProperty(Constants.MIN_SLEEP_TIME_IN_SEC)
				? CowinResources.getIntProperty(Constants.MIN_SLEEP_TIME_IN_SEC)
				: sleepTimeinSec;
		return sleepTimeinSec;
	}

	private static Map<Integer, List<User>> getDistrictUsersMap(List<User> users) {
		Map<Integer, List<User>> uniqueDistrictIdsMap = new HashMap<>();
		users.stream().forEach(user -> {
			{
				if (uniqueDistrictIdsMap.containsKey(user.getDistrictId())) {
					List<User> myList = uniqueDistrictIdsMap.get(user.getDistrictId());
					myList.add(user);
					uniqueDistrictIdsMap.put(user.getDistrictId(), myList);
				} else {
					List<User> myList = new ArrayList<>();
					myList.add(user);
					uniqueDistrictIdsMap.put(user.getDistrictId(), myList);
				}
			}
		});
		return uniqueDistrictIdsMap;
	}

	private static void checkSlotsAndNotify(Integer placeId, List<User> users, boolean isDistrict) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		String response;
		
		if (isDistrict) {
			response = FindSlot.GetAvailableSlotsCalendarByDistrictResponse(placeId, formatter.format(new Date()));
		} else {
			response = FindSlot.GetAvailableSlotsCalendarByPinResponse(placeId, formatter.format(new Date()));
		}

		if (StringUtils.isNotBlank(response)) {
			
			
			// now loop for all users and notify
			for (User user : users) {
				int userPlaceId;
				if(isDistrict)
					userPlaceId = user.getDistrictId();
				else
					userPlaceId = user.getPin();

				if (userPlaceId == placeId) {
					List<SlotInfo> slots = FindSlot.getAllAvailableSlots(response, placeId, user, isDistrict);

					if (slots.size() > 0) {
						notifyUser(user, slots);
					} else {
						/*
						 * String append = ""; if (user.isAnyVaccinePreferred()) { append =
						 * " for preferred vaccine " + user.getPreferredVaccine(); } else { append =
						 * " for all vaccine types."; }
						 * 
						 * System.out.println("No slots found for user " + user.getName() +
						 * " for district " + user.getDistrictName() + " for minAge: " +
						 * user.getMinAge() + append);
						 */
					}
				}
			}
		}
	}

	private static boolean isUserElgibleForPush(User user, String pushContent) {

		int maxNoOfTimesSamePush = CowinResources.getIntProperty(Constants.MAX_TIMES_SAME_PUSH_SEND_COUNT);

		if (StringUtils.equals(user.getLastNotificationSent(), pushContent)) {

			if (user.getLastSamePushCount() >= maxNoOfTimesSamePush) {
				// why keep sending same push over and over
				return false;
			} else {
				user.setLastSamePushCount(user.getLastSamePushCount() + 1);
				return true;
			}

		} else {
			user.setLastSamePushCount(1);
			user.setLastNotificationSent(pushContent);
		}

		return true;
	}

	private static void notifyUser(User user, List<SlotInfo> slots) throws IOException {

		String title = null;
		if (slots.stream().findFirst().filter(slot -> slot.getPincode() == user.getPin()).isPresent()) {
			title = "Hi " + user.getName() + "! Found open slot(s) at your Pin: " + user.getPin() + " BOOK ASAP!";
		} else {
			if (user.isNotifyOnlyWhenSlotInPin()) {
				return;
			}
			title = "Hi " + user.getName() + "! Found open slot(s) in your District: " + user.getDistrictName()
					+ " BOOK NOW!";
		}

		if (user.isDose1Preferred()
				&& slots.stream().findFirst().filter(slot -> slot.getDose1Capacity() > 10).isPresent()) {
			// more than 10 slots available
			title = "Hi " + user.getName() + "! More than 10 slots available in some session!! Book NOW!!";
		}

		if (user.isDose2Preferred()
				&& slots.stream().findFirst().filter(slot -> slot.getDose2Capacity() > 10).isPresent()) {
			// more than 10 slots available
			title = "Hi " + user.getName() + "! More than 10 slots available in some session!! Book NOW!!";
		}

		// create body
		StringBuilder body = buildNotificationBody(user, slots);

		if (!isUserElgibleForPush(user, body.toString())) {
			// System.out.println("Reached limit for sending push with same content. Not
			// notifying the user: " + user.getName()
			// + " for district: " + user.getDistrictName());
			return;
		}

		System.out.println("Sending Push to " + user.getName() + " for district: " + user.getDistrictName()
				+ " for minAge:" + user.getMinAge() + " with Content: " + body.toString());

		PushNotificationManager.sendNotification(title,
				StringUtils.substring(new Date() + "\n" + body.toString(), 0, 3000), user.getGcmKey(),
				user.getGcmToken());
		user.setPushCount(user.getPushCount() + 1);
	}

	private static StringBuilder buildNotificationBody(User user, List<SlotInfo> slots) {
		StringBuilder body = new StringBuilder();

		for (SlotInfo slot : slots) {

			if (user.isNotifyOnlyWhenSlotInPin()) {
				if (slot.getPincode() == user.getPin()) {
					body.append(slot.toString());
				} else {
					continue;
				}
			} else {
				body.append(slot.toString());
			}
		}
		return body;
	}

	private static void serverStartContinuityNotification(String title, List<User> users) throws IOException {

		for (User user : users) {
			System.out.println("Sending Push to " + user.getName() + " for district: " + user.getDistrictName()
					+ " for minAge:" + user.getMinAge() + " with Content: " + user.getUserDetails());
			PushNotificationManager.sendNotification(title, StringUtils.substring(user.getUserDetails(), 0, 3000),
					user.getGcmKey(), user.getGcmToken());
			user.setPushCount(user.getPushCount() + 1);
		}
	}

	private static void resetUserPushCounts(List<User> users) {
		for (User user : users) {
			System.out.println(user.getName() + " received " + user.getPushCount() + " in the last "
					+ CowinResources.getIntProperty(Constants.SERVER_LIVE_CHECK_INTERVAL_IN_SEC) / 60
					+ " minutes. Resetting.");
			user.setPushCount(0);
		}
	}

	private static void printUserPushStats(List<User> users) {
		for (User user : users) {
			System.out.println("User:" + user.getName() + " for district " + user.getDistrictName() + " for minAge :"
					+ user.getMinAge() + " Total Pushes Count :" + user.getPushCount());
		}
	}
}
