package com.evgeni.rp.firebase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.evgeni.rp.PiController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Component
public class FirebaseImpl {
	private static final Logger logger = LoggerFactory.getLogger(FirebaseImpl.class);

	private FirebaseDatabase database;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private String sofiaTemp;
	@Autowired
	private PiController piController;

	public FirebaseImpl() {
		// Fetch the service account key JSON file contents
		InputStream serviceAccount = null;
		try {
			serviceAccount = new ClassPathResource("temperature-4e323-firebase-adminsdk-080r6-6626a5dbee.json")
					.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://temperature-4e323.firebaseio.com").build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FirebaseApp.initializeApp(options);
		database = FirebaseDatabase.getInstance();
		restTemplate = new RestTemplate();
		sofiaTemp = "https://api.openweathermap.org/data/2.5/forecast?q=Sofia,BG&appid=64f84ae997222d143f45533b4f8a8149&units=metric";
		objectMapper = new ObjectMapper();
	}

	@Scheduled(fixedRate = 30 * 60 * 1000)
	public void handle() {

		ResponseEntity<String> response = restTemplate.getForEntity(sofiaTemp, String.class);
		JsonNode root = null;
		try {
			root = objectMapper.readTree(response.getBody());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tempOutside = null;
		try {
			JsonNode name = root.path("list").get(0).get("main").get("temp");
			tempOutside = name.asText();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// As an admin, the app has access to read and write all data, regardless of
		// Security Rules
		DatabaseReference ref = database.getReference("/report");
		DatabaseReference refOutside = database.getReference("/reportOutside");

		String temp = piController.getTemperature();
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

		Map<String, Object> res = createReport(temp, timeStamp);

		ref.push().setValueAsync(res);
		if (!StringUtils.isEmpty(tempOutside)) {
			Map<String, Object> resOutside = createReport(tempOutside, timeStamp);
			refOutside.push().setValueAsync(resOutside);
		}
		logger.info(String.format(
				"Sent metrics to Firabase: Inside - [value = %s ; created_on = %s]; Outside - [value = %s ; created_on = %s]",
				temp, timeStamp.toString(), tempOutside, timeStamp.toString()));
	}

	private Map<String, Object> createReport(String temp, Timestamp timeStamp) {
		Map<String, Object> res = new HashMap<>();
		res.put("value", temp);
		res.put("created_on", timeStamp);
		return res;
	}
}
