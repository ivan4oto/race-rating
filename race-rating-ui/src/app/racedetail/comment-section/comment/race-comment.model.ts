export interface RaceComment {
  id: number;
  authorName: string;
  authorImageUrl: string;
  createdAt: number;
  raceId: number;
  commentText: string;
  upvoteCount: number;
  downvoteCount: number;
  userVote?: 'upvote' | 'downvote';
}

export interface VoteResultDto {
  voteRegistered: boolean;
  currentVote: boolean | null;
}

export interface CommentVoteStatus {
  commentId: number;
  isUpvote: boolean;
}
