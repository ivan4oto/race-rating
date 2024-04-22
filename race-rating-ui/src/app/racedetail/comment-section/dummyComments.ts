import {RaceComment} from "./comment/race-comment.model";

export const dummyComments: RaceComment[] = [
  {
    id: 1,
    createdAt: new Date('2021-01-01'),
    author: {
      id: 2,
      username: 'johnbones',
      name: 'John Jones',
      email: 'bones@yahoo.com',
      imageUrl: 'https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png'
    },
    commentText: 'This was a great race! Really enjoyed the track and the organization.'
  },
  {
    id: 2,
    author: {
      id: 4,
      username: 'janejane',
      name: 'Jane Smith',
      email: 'jane@abv.bg',
      imageUrl: 'https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png'
    },
    createdAt: new Date('2021-02-15'),
    commentText: 'Beautiful scenery and a well-organized event. Looking forward to the next one!'
  },
  {
    id: 3,
    author: {
      id: 5,
      username: 'alicealice',
      name: 'Alice Johnson',
      email: 'alice@gmail.com',
      imageUrl: 'https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png'
    },
    createdAt: new Date('2021-03-10'),
    commentText: 'Challenging course, but very rewarding. The support from the staff was fantastic.'
  },
  {
    id: 4,
    author: {
      id: 6,
      username: 'bobbybrown',
      name: 'Bob Brown',
      email: 'bobbybrown@gmail.com',
      imageUrl: 'https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png'
    },
    createdAt: new Date('2021-04-05'),
    commentText: 'Well managed, but the course was more difficult than I expected. Good experience overall.'
  },
  {
    id: 5,
    author: {
      id: 7,
      username: 'charliecharlie',
      name: 'Charlie Davis',
      email: 'charlito@abv.bg',
      imageUrl: 'https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png'
    },
    createdAt: new Date('2021-05-20'),
    commentText: 'Loved every moment! The race was exhilarating and the organization was top-notch.'
  }
  // ... add more comments as needed
];
