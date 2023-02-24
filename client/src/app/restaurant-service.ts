import { Restaurant, Comment } from './models'
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom, Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class RestaurantService {

	constructor(private httpClient: HttpClient) { }

	// TODO Task 2 
	// Use the following method to get a list of cuisines
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public getCuisineList(): Promise<any> {
		// Implememntation in here
		return firstValueFrom(
			this.httpClient.get<any>('csf-assessment-production.up.railway.app/api/cuisines')
		)
	}

	// TODO Task 3 
	// Use the following method to get a list of restaurants by cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public getRestaurantsByCuisine(cuisine: string): Promise<Restaurant[]> {
		// Implememntation in here
		return firstValueFrom(
			this.httpClient.get<Restaurant[]>(`csf-assessment-production.up.railway.app/api/${cuisine}/restaurants`)
		)
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public getRestaurant(restaurantId: string): Promise<Restaurant> {
		// Implememntation in here
		return firstValueFrom(
			this.httpClient.get<Restaurant>(`csf-assessment-production.up.railway.app/api/restaurant/${restaurantId}`)
		)
	}

	// TODO Task 5
	// Use this method to submit a comment
	// DO NOT CHANGE THE METHOD'S NAME OR SIGNATURE
	public postComment(comment: Comment): Promise<Comment> {
		// Implememntation in here
		return firstValueFrom(
			this.httpClient.post<Comment>(`csf-assessment-production.up.railway.app/api/comments/${comment.restaurantId}`, comment)
		)
	}

	public getImage(lat: number, lng: number): Observable<any> {
		console.info('>>> latlng in service: ', lat, lng)
		const params = new HttpParams()
			.set("lat", lat)
			.set("lng", lng)
		const headers = new HttpHeaders()
			.set("content-type", "image/png")
			.set("Access-Control-Allow-Origin", "*")
		return this.httpClient.get<any>(`http://map.chuklee.com/map?lat=${lat}&lng=${lng}`, { params: params, headers: headers })

	}
}
