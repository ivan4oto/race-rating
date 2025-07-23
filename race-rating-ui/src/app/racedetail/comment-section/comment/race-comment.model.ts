export interface RaceComment {
  id: number;
  authorName: string;
  authorImageUrl: string;
  createdAt: number;
  raceId: number;
  commentText: string;
  upvoteCount: number;
  downvoteCount: number;
}

export interface VoteResultDto {
  voteRegistered: boolean;
  currentVote: boolean | null;
}
