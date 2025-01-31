package nz.ac.vuw.engr301.group9mcs.externaldata.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nz.ac.vuw.engr301.group9mcs.commons.OWWeatherData;
import nz.ac.vuw.engr301.group9mcs.commons.logging.DefaultLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class connects to the OpenWeatherMap one call API and retrieves weather data from it.
 * The weather data is returned in the JSON format.
 * The JSON will be parsed and the info will be pushed to all other packages that need it.
 *
 * @author Sai Panda
 * Copyright (C) 2020, Mission Control Group 9
 */
public class OWMGetter {
	/**
	 * The appid for the OpenWeatherMap API. The user will supply their own token that will be used.
	 */
	private String appid = "ead647e24776f26ed6f63af5f1bbf68c";


	/**
	 * Constructor with the user supplies their API appid.
	 * @param appid the appid the user has supplied.
	 */
	public OWMGetter(String appid) {
		this.appid = appid;
	}

	/**
	 * Validates the supplied latitiude and longitude to see if they fit within the required range
	 * @param latitude - the latitude to validate
	 * @param longitude - the longitude to validate
	 * @throws InvalidParameterException if the supplied parameters are not valid
	 */
	public static void checkValidLatAndLon(double latitude, double longitude) throws InvalidParameterException{
		if ((latitude < -90 || latitude > 90) && (longitude < -180 || longitude > 180)) {
			throw new InvalidParameterException("Latitude must be within the range [-90, 90] and Longitude must be within the range [-180, 180]");
		} else if (latitude < -90 || latitude > 90) {
			throw new InvalidParameterException("Latitude must be within the range [-90, 90]");
		} else if (longitude < -180 || longitude > 180) {
			throw new InvalidParameterException("Longitude must be within the range [-180, 180]");
		}
	}

	/**
	 * Gets the current weather at the supplied latitude and longitude.
	 * @param latitude - latitude of the location. (must be within range [-90, 90])
	 * @param longitude - longitude of the location. (must be within range [-180, 180])
	 * @throws IOException - if the API does not output correctly.
	 * @throws InvalidParameterException - if the latitude and/or longitude are incorrect.
	 * @return OWWeatherData - the parsed version of the data returned by the API call.
	 */
	@SuppressWarnings("null")
	public OWWeatherData getWeatherData(double latitude, double longitude) throws IOException, InvalidParameterException {
		try {
			checkValidLatAndLon(latitude, longitude);
			String units = "metric";
			String urlString = "https://api.openweathermap.org/data/2.5/onecall?"
					+ "lat=" + latitude + "&lon=" + longitude + "&units=" + units + "&exclude=daily,hourly,minutely&appid=" + this.appid;

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {

				JSONObject weatherJSON = new JSONObject(reader.readLine());

				JSONObject currentData = weatherJSON.getJSONObject("current");

				reader.close();
				return parseWeatherJSON(currentData);
			} catch (JSONException e) {
				DefaultLogger.logger.error(e.getMessage());
				throw e;
			}
		} catch (IOException | InvalidParameterException e) {
			DefaultLogger.logger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Get the hourly forecasts for the next 48 hours.
	 * @param latitude - latitude of the location. (must be within range [-90, 90])
	 * @param longitude - longitude of the location. (must be within range [-180, 180])
	 * @throws IOException - if the API does not output correctly.
	 * @throws InvalidParameterException - if the latitude and/or longitude are incorrect.
	 * @return OWWeatherData - the parsed version of the data returned by the API call.
	 */
	public Map<Date, OWWeatherData> getForecast(double latitude, double longitude)  throws IOException, InvalidParameterException {
		Map<Date, OWWeatherData> forecasts = new HashMap<>();
		try {
			checkValidLatAndLon(latitude, longitude);
			String units = "metric";
			String urlString = "https://api.openweathermap.org/data/2.5/onecall?"
					+ "lat=" + latitude + "&lon=" + longitude + "&units=" +
					units + "&exclude=current,daily,minutely&appid=" + this.appid;

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
				JSONObject weatherJSON = new JSONObject(reader.readLine());
				JSONArray hourlyForecasts = weatherJSON.getJSONArray("hourly");

				for (int i = 0; i < hourlyForecasts.length(); i++) {
					JSONObject forecast = hourlyForecasts.getJSONObject(i);

					long unixTime = forecast.getLong("dt");

					// Converts Unix time to millis
					Date date = new Date(unixTime * 1000);

					forecasts.put(date, parseWeatherJSON(forecast));
				}

			} catch (JSONException e) {
				DefaultLogger.logger.error(e.getMessage());
				throw e;
			}
		} catch (IOException | InvalidParameterException e) {
			DefaultLogger.logger.error(e.getMessage());
			throw e;
		}
		return forecasts;
	}

	/**
	 * Parses and returns the appropriate weather data from the supplied JSON
	 * @param weatherJSON the JSON object containing all weather info from the API
	 * @return OWWeatherData with all needed weather attributes
	 * @throws JSONException Thrown if JSON can't be parse properly
	 */
	private static OWWeatherData parseWeatherJSON(JSONObject weatherJSON) throws JSONException {
		JSONObject rainData = null;

		try {
			if (weatherJSON.has("rain")) {
				rainData = weatherJSON.getJSONObject("rain");
			}

			double temperature = weatherJSON.getDouble("temp");

			// The units for the wind speed returned by the API is in meters per second.
			// So we need to convert it to kilometers per hour as that is the standard unit of measurement for wind in New Zealand.
			double windSpeed = ((weatherJSON.getDouble("wind_speed") * 60) * 60) / 1000;

			// Wind direction (meteorological)
			double windDegrees = weatherJSON.getDouble("wind_deg");

			// Atmospheric Pressure in hPa
			double pressure = weatherJSON.getDouble("pressure");

			// Amount of Rainfall in the last hour.
			double precipitation = rainData != null && rainData.keySet().contains("1h") ? rainData.getDouble("1h") : 0.0;

			// Current Humidity in percentage
			double humidity = weatherJSON.getDouble("humidity");

			// Cloudiness percentage
			double cloudiness = weatherJSON.getDouble("clouds");

			return new OWWeatherData(temperature, windSpeed, windDegrees, pressure, precipitation, humidity, cloudiness);
		} catch (JSONException e) {
			throw new JSONException("JSON returned by the API couldn't be parsed properly: " + e.getMessage());
		}

	}
	/**
	 * get the appid for the API.
	 * @return the appid.
	 */
	public String getAppId() {
		return this.appid;
	}

	/**
	 * set the appid for the API.
	 * @param appid the appid to set.
	 */
	public void setAppId(String appid) {
		this.appid = appid;
	}

	/**
	 * Checks if the user can successfully connect to the OpenWeatherMap API.
	 * @return true if the user can connect to the API, false otherwise.
	 */
	public static boolean isAvailable() {
		try {
	         URL url = new URL("http://openweathermap.org/");
	         URLConnection connection = url.openConnection();
	         connection.connect();
	      } catch (IOException e) {
	    	  DefaultLogger.logger.error(e.getMessage());
	         return false;
	      }
		return true;
	}
}
