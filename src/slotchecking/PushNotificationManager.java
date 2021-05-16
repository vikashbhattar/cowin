package slotchecking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import util.Constants;

public class PushNotificationManager {

	public static void sendNotification(String title, String content, String gcmKey, String gcmToken)
			throws IOException {
		URL url = new URL(Constants.GCM_SEND_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=" + gcmKey);
		con.setInstanceFollowRedirects(true);
		con.setDoOutput(true);

		String requestBodyPayload = createGCMNotificationPayload(title, content, gcmToken);
		//System.out.println(requestBodyPayload);

		try (OutputStream os = con.getOutputStream()) {
			byte[] input = requestBodyPayload.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		InputStream responseStream = con.getInputStream();
		InputStreamReader isReader = new InputStreamReader(responseStream);
		// Creating a BufferedReader object
		BufferedReader reader = new BufferedReader(isReader);
		StringBuffer sb = new StringBuffer();
		String str;
		while ((str = reader.readLine()) != null) {
			sb.append(str);
		}
		JSONObject jsonRes = new JSONObject(sb.toString());
		if(jsonRes.getInt("success") == 1) {
			System.out.println("Message sent sucessfully! Title:" + title  + "\n");
		} else {
			System.out.println("Message delivery failed. Reason: " + jsonRes.getJSONArray("results").getJSONObject(0).getString("error"));
		}
	}

	private static String createGCMNotificationPayload(String title, String content, String gcmToken) {

		JSONObject json = new JSONObject();
		json.put("to", gcmToken);

		JSONObject notification = new JSONObject();
		notification.put("body", content);
		notification.put("title", title);

		JSONObject data = new JSONObject();
		// data.put("content", content);
		data.put("title", title);

		json.put("notification", notification);
		json.put("data", data);
		json.put("priority", "high");

		return json.toString();
	}
}
