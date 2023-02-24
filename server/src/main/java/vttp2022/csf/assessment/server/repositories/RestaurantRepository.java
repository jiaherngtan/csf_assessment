package vttp2022.csf.assessment.server.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.LatLng;
import vttp2022.csf.assessment.server.models.Restaurant;

@Repository
public class RestaurantRepository {

	public static final String C_RESTAURANT = "restaurants";

	@Autowired
	private MongoTemplate mongoTemplate;

	// TODO Task 2
	// Use this method to retrive a list of cuisines from the restaurant collection
	// You can add any parameters (if any) and the return type
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	//
	// db.getCollection("restaurants").distinct("cuisine")
	public List<String> getCuisines() {
		// Implmementation in here
		return mongoTemplate.findDistinct(new Query(), "cuisine", C_RESTAURANT, String.class);
	}

	// TODO Task 3
	// Use this method to retrive a all restaurants for a particular cuisine
	// You can add any parameters (if any) and the return type
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	//
	// db.restaurants.aggregate([
	// { $match: { cuisine: "Afghan" }},
	// { $sort: { name: 1 }}
	// ])
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here
		MatchOperation matchOps = Aggregation.match(
				Criteria.where("cuisine").is(cuisine));

		SortOperation sortOps = Aggregation.sort(Sort.by(Direction.ASC, "name"));

		Aggregation pipeline = Aggregation.newAggregation(matchOps, sortOps);

		AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, C_RESTAURANT, Document.class);

		List<Restaurant> restaurants = new LinkedList<>();
		for (Document d : results)
			restaurants.add(create(d));

		return restaurants;
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any)
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	//
	// db.restaurants.aggregate([
	// {
	// $match: {
	// restaurant_id: "40552806"
	// }
	// },
	// {
	// $project: {
	// address_concat: { $concat: [ "$address.building", ", ",
	// "$address.street", ", ",
	// "$address.zipcode", ", ",
	// "$borough"] },
	// coordinates: "$address.coord",
	// _id: 0,
	// restaurant_id: 1,
	// name: 1,
	// cuisine: 1,
	// address: 1,
	// coord: 1
	// }
	// }
	// ])
	public Optional<Restaurant> getRestaurant(String restaurantId) {
		// Implmementation in here
		Criteria c = Criteria.where("restaurant_id").is(restaurantId);

		Query q = Query.query(c);

		Document d = mongoTemplate.findOne(q, Document.class, C_RESTAURANT);

		return Optional.of(create(d));
	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	//
	// db.comments.insert({
	// restaurantId: 'some Id',
	// name: 'some name',
	// rating: 1,
	// text: 'some text'
	// })
	public void addComment(Comment comment) {
		// Implmementation in here
		System.out.println("add comment... repo");
		JsonObject jo = Json.createObjectBuilder()
				.add("restaurantId", comment.getRestaurantId())
				.add("name", comment.getName())
				.add("rating", comment.getRating())
				.add("text", comment.getText())
				.build();
		Document doc = Document.parse(jo.toString());
		mongoTemplate.insert(doc, "comments");
	}

	// You may add other methods to this class
	public static Restaurant create(Document d) {
		Restaurant r = new Restaurant();
		r.setRestaurantId(d.getString("restaurant_id"));
		r.setName(d.getString("name"));
		r.setCuisine(d.getString("cuisine"));
		Document addressDoc = d.get("address", Document.class);
		String building = addressDoc.getString("building");
		String street = addressDoc.getString("street");
		String zipcode = addressDoc.getString("zipcode");
		String borough = d.getString("borough");
		String address = building + ", " + street + ", " + zipcode + ", " + borough;
		r.setAddress(address);
		List<Double> latlngDoc = addressDoc.get("coord", List.class);
		LatLng latlng = new LatLng();
		System.out.println(latlngDoc.get(0).getClass().getSimpleName());
		latlng.setLatitude(latlngDoc.get(1).floatValue());
		latlng.setLongitude((float) latlngDoc.get(0).floatValue());
		r.setCoordinates(latlng);

		return r;
	}
}
