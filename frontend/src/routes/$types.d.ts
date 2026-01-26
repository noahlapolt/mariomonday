// TODO remove all IDs or something.

type Bracket = {
  id: string;
  date: string;
  gameType: string;
  rounds: number;
  winners: string[];
  losers: string[];
  gameSets: GameSet[][];
  teams: PlayerSet[];
};

type Game = {
  id: string;
  gameType: string;
  playerSets: PlayerSet[];
};


type GameSet = {
  id: string;
  playerSets: string[];
  winners: string[];
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
