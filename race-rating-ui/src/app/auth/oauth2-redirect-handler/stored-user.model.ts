export interface UserModel {
  id?: number,
  username: string,
  name: string,
  email: string,
  imageUrl: string,
  role: string,
  votedForRaces: number[],
  commentedForRaces: number[]
}
