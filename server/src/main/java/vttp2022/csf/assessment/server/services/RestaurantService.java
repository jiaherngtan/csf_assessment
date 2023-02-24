package vttp2022.csf.assessment.server.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.repositories.MapCache;
import vttp2022.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepo;

	@Autowired
	private MapCache mapCache;

	// TODO Task 2
	// Use the following method to get a list of cuisines
	// You can add any parameters (if any) and the return type
	// DO NOT CHNAGE THE METHOD'S NAME
	public List<String> getCuisines() {
		// Implmementation in here
		return restaurantRepo.getCuisines();
	}

	// TODO Task 3
	// Use the following method to get a list of restaurants by cuisine
	// You can add any parameters (if any) and the return type
	// DO NOT CHNAGE THE METHOD'S NAME
	public JsonArray getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here
		List<Restaurant> restaurants = restaurantRepo.getRestaurantsByCuisine(cuisine);
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		for (Restaurant restaurant : restaurants) {
			JsonArrayBuilder coordArr = Json.createArrayBuilder();
			coordArr.add(restaurant.getCoordinates().getLongitude());
			coordArr.add(restaurant.getCoordinates().getLatitude());
			JsonObject jo = Json.createObjectBuilder()
					.add("restaurantId", restaurant.getRestaurantId())
					.add("name", restaurant.getName())
					.add("cusisine", restaurant.getCuisine())
					.add("address", restaurant.getAddress())
					.add("coordinates", coordArr.build())
					.build();
			arrBuilder.add(jo);
		}
		return arrBuilder.build();
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any)
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public Optional<Restaurant> getRestaurant(String restaurantId) {
		// Implmementation in here
		return restaurantRepo.getRestaurant(restaurantId);
	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public void addComment(Comment comment) {
		// Implmementation in here
		restaurantRepo.addComment(comment);
	}

	//
	// You may add other methods to this class
	public void getImage(Integer lat, Integer lng) {
		mapCache.getMap(lat, lng);
	}
}
