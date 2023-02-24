package vttp2022.csf.assessment.server.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.services.RestaurantService;
import vttp2022.csf.assessment.server.services.S3Service;

@Controller
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private S3Service s3Service;

    @GetMapping(path = "/cuisines", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @CrossOrigin()
    public ResponseEntity<String> getCuisines() {

        System.out.println(">>> getting cuisines list");
        List<String> cuisines = restaurantService.getCuisines();

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (String cuisine : cuisines) {
            if (cuisine.contains("/"))
                cuisine = cuisine.replace("/", "_");
            arrBuilder.add(cuisine);
        }

        JsonArray result = arrBuilder.build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @GetMapping(path = "/{cuisine}/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @CrossOrigin()
    public ResponseEntity<String> getRestaurantsByCuisine(@PathVariable String cuisine) {

        System.out.println(">>> getting restaurants for " + cuisine);
        if (cuisine.contains("_"))
            cuisine = cuisine.replace("_", "/");

        JsonArray result = restaurantService.getRestaurantsByCuisine(cuisine);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @GetMapping(path = "/restaurant/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @CrossOrigin()
    public ResponseEntity<String> getRestaurant(@PathVariable String restaurantId, Integer lat, Integer lng) {

        try {
            Optional<Restaurant> ops = restaurantService.getRestaurant(restaurantId);
            Restaurant restaurant = ops.get();

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
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jo.toString());

        } catch (Exception e) {
            JsonObject jsonObj = Json.createObjectBuilder()
                    .add("error:", "Restaurant with id " + restaurantId + " not found")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObj.toString());
        }
    }

    @PostMapping(path = "/comments/{restaurantId}")
    @CrossOrigin()
    public ResponseEntity<String> addComment(
            @PathVariable String restaurantId,
            @RequestBody String payload) {

        System.out.println(payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jo = reader.readObject();
        System.out.println(">>> jo: " + jo);

        Comment c = new Comment();
        c.setRestaurantId(defaultValue(jo.getString("restaurantId"), null));
        c.setName(defaultValue(jo.getString("name"), null));
        c.setRating(defaultValue(jo.getInt("rating"), null));
        c.setText(defaultValue(jo.getString("text"), null));

        restaurantService.addComment(c);

        JsonObject result = Json.createObjectBuilder()
                .add("message", "Comment posted")
                .build();

        return ResponseEntity.status(201).body(result.toString());
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @CrossOrigin()
    public ResponseEntity<String> postUploadFromAngular(
            @RequestPart MultipartFile myImage) {

        System.out.printf("file name: %s\n", myImage.getOriginalFilename());
        System.out.printf("content type: %s\n", myImage.getContentType());

        String key = "";

        try {
            key = s3Service.upload(myImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JsonObject payload = Json.createObjectBuilder()
                .add("imageKey", key)
                .build();

        return ResponseEntity.ok(payload.toString());
    }

    public <T> T defaultValue(T actualVal, T defaultVal) {
        if (null == actualVal)
            return defaultVal;
        return actualVal;
    }

}
