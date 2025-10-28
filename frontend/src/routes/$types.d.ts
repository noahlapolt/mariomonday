type Bracket = {
  id?: string;
  date: Date;
  rounds: number;
  winners: Player[];
  gameType: string;
  players: PlayerSet[];
  gameSets: GameSet[];
  finalGameSet: GameSet;
};

type Game = {
  id?: string;
  winners: PlayerSet[];
  playerSets: PlayerSet[];
};

type GameSet = {
  id: string;
  roundIndex: number;
  winners: PlayerSet[];
  losers: PlayerSet[];
  addedPlayerSets: PlayerSet[];
  gameType: string;
  previousGameSets: GameSet[];
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
  players: Player[];
  name: string;
};
