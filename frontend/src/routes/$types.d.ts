type Bracket = {
  id?: string;
  date: Date;
  rounds: number;
  winners: Set<Player>;
  gameType: string;
  players: Set<PlayerSet>;
  gameSets: Set<GameSet>;
  finalGameSet: GameSet;
};

type Game = {
  id?: string;
  winners: Set<PlayerSet>;
  playerSets: Set<PlayerSet>;
};

type GameSet = {
  id: string;
  roundIndex: number;
  winners: Set<PlayerSet>;
  losers: Set<PlayerSet>;
  addedPlayerSets: Set<PlayerSet>;
  gameType: string;
  previousGameSets: Set<GameSet>;
  games: Game[];
};

type GameType = {
  maxPlayerSets: number,
  playerSetsToMoveOn: number,
  playersOnATeam: number,
};

type Player = {
  id?: string;
  name: string;
  eloMap: Record<string, number>;
};

type PlayerSet = {
  id?: string;
  players: Set<Player>;
  name: string;
};