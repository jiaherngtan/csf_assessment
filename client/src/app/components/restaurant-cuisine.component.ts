import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Restaurant } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-cuisine',
  templateUrl: './restaurant-cuisine.component.html',
  styleUrls: ['./restaurant-cuisine.component.css']
})
export class RestaurantCuisineComponent implements OnInit {

  // TODO Task 3
  // For View 2
  cuisine!: string
  restaurants: Restaurant[] = []
  params$!: Subscription

  constructor(
    private activatedRoute: ActivatedRoute,
    private restaurantService: RestaurantService) { }

  ngOnInit(): void {
    this.params$ = this.activatedRoute.params.subscribe(
      (params) => {
        this.cuisine = params['cuisine']
        console.info('>>> cuisine: ', this.cuisine)
        this.restaurantService.getRestaurantsByCuisine(this.cuisine)
          .then(result => {
            console.info('>>> result: ', result)
            this.restaurants = result
          })
          .catch(error => {
            console.error('>>> error: ', error)
          })
      })
  }

}
