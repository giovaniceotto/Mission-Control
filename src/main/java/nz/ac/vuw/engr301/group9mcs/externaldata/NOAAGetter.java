package nz.ac.vuw.engr301.group9mcs.externaldata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import nz.ac.vuw.engr301.group9mcs.commons.WeatherData;

/**
 * This class connects to the OpenWeatherMap one call API and retrieves weather data from it.
 * The weather data is returned in the JSON format.
 * The JSON will be parsed and the info will be pushed to all other packages that need it.
 *
 *
 * @author Sai Panda, pandasai, 300449188
 */
public class NOAAGetter {
	/*
	 * The appid for the OpenWeatherMap API. The user will supply their own token that will be used.
	 */
	private String appid = "ead647e24776f26ed6f63af5f1bbf68c";


	/**
	 * Constructor with the user supplies their API appid.
	 * @param appid the appid the user has supplied.
	 */
	public NOAAGetter(String appid) {
		this.appid = appid;
	}

	/**
	 * Gets the current weather at the supplied latitude and longitude.
	 * @param latitude - latitude of the location.
	 * @param longitude - longitude of the location.
	 * @return WeatherData with the data returned by the API call.
	 */
	public WeatherData getWeatherData(double latitude, double longitude) {
		try {
			String units = "metric";
			String urlString = "https://api.openweathermap.org/data/2.5/onecall?"
					+ "lat=" + latitude + "&lon=" + longitude + "&units=" + units + "&exclude=daily,hourly,minutely&appid=" + this.appid;

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));){

				JSONObject weatherJSON = new JSONObject(reader.readLine());

				JSONObject currentData = weatherJSON.getJSONObject("current");

                JSONObject rainData = null;

                if(currentData.has("rain")) {
                    rainData = currentData.getJSONObject("rain");
                }

				double temperature = currentData.getDouble("temp");

				// The units for the wind speed returned by the API is in meters per second.
				// So we need to convert it to kilometers per hour as that is the standard unit of measurement for wind in New Zealand.
				double windSpeed = ((currentData.getDouble("wind_speed") * 60) * 60) / 1000;
					
	            // Wind direction (meteorological) 
                double windDegrees = currentData.getDouble("wind_deg");
				
                // Atmospheric Pressure in hPa
				double pressure = currentData.getDouble("pressure");

                // Amount of Rainfall in the last hour.
                double precipitation = rainData != null && rainData.keySet().contains("1h") == true ? rainData.getDouble("1h") : 0.0;
                
                // Current Humidity in percentage 
                double humidity = currentData.getDouble("humidity");
                
                // Cloudiness percentage 
                double cloudiness = currentData.getDouble("clouds");
                
				reader.close();
				return new WeatherData(temperature, windSpeed, windDegrees, pressure, precipitation, humidity, cloudiness);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block.
			e.printStackTrace();
		}
		return new WeatherData(0, 0, 0, 0, 0, 0, 0); //TODO change this
	}

	/**
	 * Get the hourly forecast for the next 48 hours.
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public List<WeatherData> getForecast(double latitude, double longitude){
		try {
			String units = "metric";
			String urlString = "https://api.openweathermap.org/data/2.5/onecall?"
					+ "lat=" + latitude + "&lon=" + longitude + "&units=" +
					units + "&exclude=current,daily,minutely&appid=" + this.appid;

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
				JSONObject weatherJSON = new JSONObject(reader.readLine());

				JSONArray hourlyforecasts = weatherJSON.getJSONArray("hourly");

				for(int i = 0; i < hourlyforecasts.length(); i++){
					JSONObject forecast = hourlyforecasts.getJSONObject(i);

					long unixTime = forecast.getLong("dt");

					Date date = new Date(unixTime * 1000);
					SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					String formattedDate = sdf.format(date);
					System.out.println(formattedDate);

					//System.out.println(forecast.getLong("dt"));
				}
				System.out.println(hourlyforecasts);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	/**
	 * @return the appid.
	 */
	public String getAppId() {
		return this.appid;
	}

	/**
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
	      } catch (MalformedURLException e) {
	         return false;
	      } catch (IOException e) {
	         return false;
	      }
		return true;
	}


	/**
	 * Test main method.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Connection to OWM: " + (NOAAGetter.isAvailable() == true ? "Successful" : "Failed"));
		System.out.println();

		NOAAGetter getter = new NOAAGetter("ead647e24776f26ed6f63af5f1bbf68c");
		System.out.println(getter.getForecast(-41.289224, 174.768352));

	}

}
