export interface RaceListModel {
  id: number,
  name: string,
  description: string,
  averageRating: number,
  averageTraceScore: number,
  averageVibeScore: number,
  averageOrganizationScore: number,
  averageLocationScore: number,
  averageValueScore: number,
  ratingsCount: number,
  latitude: number,
  longitude: number,
  rating: number,
  logoUrl: string,
  websiteUrl: string,
  eventDate: Date,
}

export interface RaceSummaryDto {
  id: number;
  name: string;
  logoUrl: string;
  averageRating: number;
  ratingsCount: number;
  totalComments: number;
  totalVotes: number;
  eventDate: Date;
}
