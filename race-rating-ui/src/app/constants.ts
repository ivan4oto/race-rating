import {Terrain} from "./racelist/racelist.component";

export const TOASTR_SUCCESS_HEADER = 'Success!';
export const TOASTR_ERROR_HEADER = 'Error!';

export const FILTER_MINIMAL_DISTANCE = 0;
export const FILTER_MAXIMUM_DISTANCE = 180;
export const FILTER_MINIMAL_ELEVATION = 0;
export const FILTER_MAXIMUM_ELEVATION = 9000;

export const RATINGS_CYRILIC = {
  'trace': 'Трасе ',
  'organization': 'Организация',
  'atmosphere': 'Атмосфера',
  'price': 'Цена',
  'location': 'Локация'
}

export const TERRAINS: Terrain[] = [
  {
    name: 'flat',
    checked: true,
    color: 'primary'
  },
  {
    name: 'technical trail',
    checked: true,
    color: 'primary'
  },
  {
    name: 'big mountain',
    checked: true,
    color: 'primary'
  },
  {
    name: 'road',
    checked: true,
    color: 'primary'
  },
  {
    name: 'trail',
    checked: true,
    color: 'primary'
  }
]
