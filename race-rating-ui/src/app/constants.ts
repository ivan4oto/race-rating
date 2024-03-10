import {Terrain} from "./racelist/racelist.component";

export const FILTER_MINIMAL_DISTANCE = 0;
export const FILTER_MAXIMUM_DISTANCE = 180;
export const FILTER_MINIMAL_ELEVATION = 0;
export const FILTER_MAXIMUM_ELEVATION = 9000;

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
