package demo;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java DestinationHashGenerator <PRN Number> <JSON file>");
			System.exit(1);
		}

		String prnNumber = args[0].toLowerCase().replaceAll("\\s", "");
		String jsonFilePath = args[1];

		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(jsonFilePath));
			String destinationValue = findDestinationValue(jsonObject, "destination");
			if (destinationValue == null) {
				System.err.println("The key 'destination' was not found in the JSON file.");
				System.exit(1);
			}

			String randomString = generateRandomString(8);
			String concatenatedString = prnNumber + destinationValue + randomString;

			System.out.println(generateMD5Hash(concatenatedString) + ";" + randomString);
		} catch (IOException | ParseException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static String findDestinationValue(JSONObject jsonObject, String key) {
		if (jsonObject.containsKey(key)) {
			return jsonObject.get(key).toString();
		}

		for (Object k : jsonObject.keySet()) {
			if (jsonObject.get(k) instanceof JSONObject) {
				String value = findDestinationValue((JSONObject) jsonObject.get(k), key);
				if (value != null) {
					return value;
				}
			}
		}

		return null;
	}

	private static String generateRandomString(int length) {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder randomString = new StringBuilder(length);
		Random random = new Random();

		for (int i = 0; i < length; i++) {
			randomString.append(characters.charAt(random.nextInt(characters.length())));
		}

		return randomString.toString();
	}

	private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashBytes = md.digest(input.getBytes());

		StringBuilder hashString = new StringBuilder();
		for (byte b : hashBytes) {
			hashString.append(String.format("%02x", b));
		}

		return hashString.toString();
	}
}
