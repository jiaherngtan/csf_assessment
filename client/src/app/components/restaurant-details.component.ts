import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Restaurant, Comment } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.css']
})
export class RestaurantDetailsComponent implements OnInit {

  // TODO Task 4 and Task 5
  // For View 3
  restaurantId!: string
  lat!: number
  lng!: number
  imageData = ""
  imageUrl = ""
  restaurant!: Restaurant
  params$!: Subscription
  imgParams$!: Subscription
  blob!: Blob
  form!: FormGroup

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private fb: FormBuilder) { }

  ngOnInit(): void {
    this.params$ = this.activatedRoute.params.subscribe(
      (params) => {
        this.restaurantId = params['restaurantId']
        console.info('>>> restaurantId: ', this.restaurantId)
        this.restaurantService.getRestaurant(this.restaurantId)
          .then(result => {
            console.info('>>> result: ', result)
            this.restaurant = result
            this.lat = result.coordinates.at(1)!
            this.lng = result.coordinates.at(0)!
            this.imageUrl = `http://map.chuklee.com/map?lat=${this.lat}&lng=${this.lng}`
            console.info('>>> latlng: ', this.lat, this.lng)
            console.info('>>> latlng before service: ', this.lat, this.lng)
            this.restaurantService.getImage(this.lat, this.lng)
              .subscribe((resp) => {
                console.info('resp: ', resp)
                this.imageData = resp
              })
          })
          .catch(error => {
            console.error('>>> error: ', error)
          })
      })
    this.form = this.createForm()
  }

  private createForm(): FormGroup {
    return this.fb.group({
      restaurantId: this.fb.control<string>(''),
      name: this.fb.control<string>('', [Validators.required, Validators.minLength(4)]),
      rating: this.fb.control<number>(1, [Validators.required, Validators.min(1), Validators.max(5)]),
      text: this.fb.control<string>('', [Validators.required]),
    })
  }

  processForm() {
    const formData = new FormData()
    formData.set('name', this.form.get('name')?.value)
    formData.set('rating', this.form.get('rating')?.value)
    formData.set('text', this.form.get('text')?.value)
    const comm = this.form.value as Comment
    comm.restaurantId = this.restaurantId
    console.info('>>> post: ', comm)

    this.restaurantService.postComment(comm)
      .then(result => {
        console.info('>>> result: ', result)
        this.router.navigate(['/'])
      })
      .catch(error => {
        console.error('>>> error: ', error)
      })
  }

  dataURItoBlob(dataURI: string) {
    // convert base64 to raw binary data held in a string
    // doesn't handle URLEncoded DataURIs - see SO answer #6850276 for code that does this
    var byteString = atob(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to an ArrayBuffer
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }

    //Old Code
    //write the ArrayBuffer to a blob, and you're done
    //var bb = new BlobBuilder();
    //bb.append(ab);
    //return bb.getBlob(mimeString);

    //New Code
    return new Blob([ab], { type: mimeString });
  }

}
