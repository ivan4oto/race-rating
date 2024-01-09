export interface RatingModel {
  id?: number,
  raceId: number,
  authorId?: number,
  traceScore: number,
  vibeScore: number,
  organizationScore: number,
  locationScore: number,
  valueScore: number
  createdAt?: Date
}
