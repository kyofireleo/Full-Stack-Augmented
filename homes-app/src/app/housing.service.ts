import { Injectable } from '@angular/core';
import { HousingLocation } from './housing-location';

@Injectable({
  providedIn: 'root'
})
export class HousingService {
  url = 'http://localhost:8080/locations';

  constructor() { }

  async getAllHousingLocations() : Promise<HousingLocation[]>{
    const data = await fetch(this.url+"/all/");
    var returnData = await data.json() ?? {}; 
    return returnData.data ?? [];
  }

  async getHousingLocationById(id: Number): Promise<HousingLocation | undefined>{
    const data = await fetch(`${this.url}/byId/${id}`);
    var returnData = await data.json() ?? {}; 
    return returnData.data ?? {};
  }

  async createNewHousingLocation(location: HousingLocation): Promise<HousingLocation>{
    const options = {
      method: 'PUT',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(location)
    }
    
    const data = await fetch(this.url+"/new", options);
    var returnData = await data.json() ?? {};
    return returnData.data ?? {};
  }

  async deleteHousingLocationById(id: Number): Promise<Response> {
    const options = {
      method: 'DELETE',
      headers: {'Access-Control-Allow-Origin':'*'}
    }
    
    const data = await fetch(`${this.url}/delete/byId/${id}`, options);
    var returnData = await data.json() ?? {};
    return returnData;
  }

  async updateHousingLocation(location: HousingLocation): Promise<HousingLocation>{
    const options = {
      method: 'PATCH',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(location)
    }
    
    const data = await fetch(this.url+"/update/", options);
    var returnData = await data.json() ?? {};
    return returnData.data ?? {};
  }

  submitApplication(firstName: string, lastName: string, email: string){
    console.log(firstName, lastName, email);
  }
}
