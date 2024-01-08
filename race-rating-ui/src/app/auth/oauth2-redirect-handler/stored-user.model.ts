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
