// TODO remove all IDs or something.

type Bracket = {
  id: string;
  date: Date;
  gameType: string;
  winners: PlayerSet[];
  losers: PlayerSet[];
  gameSets: GameSet[][];
};

type Game = {
  id: string;
  winners: PlayerSet[];
  playerSets: PlayerSet[];
};

type GameSet = {
  id: string;
  playerSets: PlayerSet[];
  winners: PlayerSet[];
  previousGameSets: string[];
  games: Game[];
};

type GameType = {
  maxPlayerSets: number,
  playerSetsToMoveOn: number,
  playersOnATeam: number,
};

type Player = {
  id: string;
  name: string;
  eloMap: Record<string, number>;
};

type PlayerSet = {
  id: string;
  players: Player[];
  name: string;
};
