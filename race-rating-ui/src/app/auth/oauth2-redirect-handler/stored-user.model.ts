export interface StoredUserModel {
  data: StoredUserModelData,
  token: string
}

export interface StoredUserModelData {
  exp: number,
  avatarUrl: string,
  email: string,
  name: string,
  rol: string[],
}

export interface UserModel {
  id: number,
  username: string,
  name: string,
  email: string,
  imageUrl: string,
  role: string,
  votedForRaces: number[]
}
