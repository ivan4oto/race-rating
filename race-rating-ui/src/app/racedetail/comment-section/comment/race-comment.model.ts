export interface RaceComment {
  id: number;
  author: {
    id: number,
    username: string,
    name: string,
    email: string,
    imageUrl: string
  };
  createdAt: Date;
  commentText: string;
}
