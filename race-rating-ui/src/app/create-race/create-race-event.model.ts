export class CreateRaceEventModel {
  name?: string;
  description?: string;
  latitude?: number;
  longitude?: number;
  websiteUrl?: string;
  logoUrl?: string;
  terrainTags?: string[];
  availableDistances?: number[];
  distance?: number;
  elevation?: number;
  eventDate?: Date;
}
