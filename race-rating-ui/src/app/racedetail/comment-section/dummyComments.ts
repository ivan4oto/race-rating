import {RaceComment} from "./comment/race-comment.model";

export const dummyComments: RaceComment[] = [
  {
    id: 1,
    author: 'John Doe',
    createdOn: new Date('2021-01-01'),
    authorAvatarUrl: 'https://lh3.googleusercontent.com/a/AATXAJynIOfHLyXgk7y9v_GWsXkFeHV0tHGMbzXsACwx=s96-c64',
    commentText: 'This was a great race! Really enjoyed the track and the organization.'
  },
  {
    id: 2,
    author: 'Jane Smith',
    createdOn: new Date('2021-02-15'),
    authorAvatarUrl: 'https://lh3.googleusercontent.com/a/AATXAJynIOfHLyXgk7y9v_GWsXkFeHV0tHGMbzXsACwx=s96-c64',
    commentText: 'Beautiful scenery and a well-organized event. Looking forward to the next one!'
  },
  {
    id: 3,
    author: 'Alice Johnson',
    createdOn: new Date('2021-03-10'),
    authorAvatarUrl: 'https://lh3.googleusercontent.com/a/AATXAJynIOfHLyXgk7y9v_GWsXkFeHV0tHGMbzXsACwx=s96-c64',
    commentText: 'Challenging course, but very rewarding. The support from the staff was fantastic.'
  },
  {
    id: 4,
    author: 'Bob Brown',
    createdOn: new Date('2021-04-05'),
    authorAvatarUrl: 'https://example.com/avatar4.jpg',
    commentText: 'Well managed, but the course was more difficult than I expected. Good experience overall.'
  },
  {
    id: 5,
    author: 'Charlie Davis',
    createdOn: new Date('2021-05-20'),
    authorAvatarUrl: 'https://lh3.googleusercontent.com/a/AATXAJynIOfHLyXgk7y9v_GWsXkFeHV0tHGMbzXsACwx=s96-c64',
    commentText: 'Loved every moment! The race was exhilarating and the organization was top-notch.'
  }
  // ... add more comments as needed
];
