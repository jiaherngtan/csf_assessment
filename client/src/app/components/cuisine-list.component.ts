import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-cuisine-list',
  templateUrl: './cuisine-list.component.html',
  styleUrls: ['./cuisine-list.component.css']
})
export class CuisineListComponent implements OnInit {

  cuisines: string[] = []
  params$!: Subscription

  // TODO Task 2
  // For View 1
  constructor(
    private activatedRoute: ActivatedRoute,
    private restaurantService: RestaurantService) { }

  ngOnInit(): void {
    this.params$ = this.activatedRoute.params.subscribe(
      (params) => {
        this.restaurantService.getCuisineList()
          .then(result => {
            console.info('>>> result: ', result)
            this.cuisines = result
          })
          .catch(error => {
            console.error('>>> error: ', error)
          })
      })
  }
}
