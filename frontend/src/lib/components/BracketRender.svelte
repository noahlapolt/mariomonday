<script lang="ts">
  import { onMount } from "svelte";
  import PlayerSetRender from "./PlayerSetRender.svelte";
  import { GameTypes } from "./Utils.svelte";

  let {
    bracketId,
  }: {
    bracketId: string;
  } = $props();

  // All brackets for testing.
  let testBrackets: Record<string, Bracket> = {
    "0": {
      id: "0",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [],
    },
    "1": {
      id: "1",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
      ],
    },
    "2": {
      id: "2",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "3": {
      id: "3",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
      ],
    },
    "4": {
      id: "4",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "5": {
      id: "5",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "6": {
      id: "6",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_SINGLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "7": {
      id: "7",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [],
    },
    "8": {
      id: "8",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
      ],
    },
    "9": {
      id: "9",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "10": {
      id: "10",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
      ],
    },
    "11": {
      id: "11",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "12": {
      id: "12",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "13": {
      id: "13",
      date: new Date(),
      gameType: "MARIO_KART_8",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Reed",
                name: "Reed",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Noah",
                name: "Noah",
                players: [
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Jack",
                name: "Jack",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
              {
                id: "Zach",
                name: "Zach",
                players: [
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { MARIO_KART_8: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "14": {
      id: "14",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [],
    },
    "15": {
      id: "15",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
      ],
    },
    "16": {
      id: "16",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "17": {
      id: "17",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
      ],
    },
    "18": {
      id: "18",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "19": {
      id: "19",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
    "20": {
      id: "20",
      date: new Date(),
      gameType: "SMASH_ULTIMATE_DOUBLES",
      winners: [],
      losers: [],
      gameSets: [
        [
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [
              {
                id: "Team A",
                name: "Team A",
                players: [
                  {
                    id: "Reed",
                    name: "Reed",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Noah",
                    name: "Noah",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
              {
                id: "Team B",
                name: "Team B",
                players: [
                  {
                    id: "Jack",
                    name: "Jack",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                  {
                    id: "Zach",
                    name: "Zach",
                    eloMap: { SMASH_ULTIMATE_SINGLES: 1500 },
                  },
                ],
              },
            ],
            winners: [],
            previousGameSets: [],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: [""],
            games: [],
          },
        ],
        [
          {
            id: "Game A",
            playerSets: [],
            winners: [],
            previousGameSets: ["", ""],
            games: [],
          },
        ],
      ],
    },
  };

  let bracket = $state(testBrackets[bracketId]);
  const gameInfo = GameTypes[bracket.gameType];
  const heightOfSet =
    40 * gameInfo.playersOnATeam + (gameInfo.playersOnATeam > 1 ? 30 : 0);
  const heightCombined = (heightOfSet + 10) * gameInfo.maxPlayerSets;
  const minGap = 20;

  /**
   * Counts up all of the wins from the recorded games and sets the
   * gameSet winners to the playerSets with the most wins.
   * @param gameSet The gameSet to resolve.
   * @param roundIndex The index of the current round.
   * @param gameSetIndex The index of the current game set.
   */
  const resolveGame = (
    gameSet: GameSet,
    roundIndex: number,
    gameSetIndex: number,
  ) => {
    // Seed the totalWinsPerSet with all players.
    let totalWinsPerSet = new Map<
      string,
      { wins: number; playerSet: PlayerSet }
    >();
    gameSet.playerSets.forEach((playerSet) => {
      totalWinsPerSet.set(playerSet.id, { wins: 0, playerSet: playerSet });
    });

    // Count up the wins.
    gameSet.games.forEach((game) => {
      game.winners.forEach((winner) => {
        let winCount = totalWinsPerSet.get(winner.id);
        if (winCount !== undefined) {
          totalWinsPerSet.set(winner.id, {
            wins: winCount.wins + 1,
            playerSet: winCount.playerSet,
          });
        } else {
          // This will should never run, but its here just in case.
          totalWinsPerSet.set(winner.id, { wins: 1, playerSet: winner });
        }
      });
    });

    // Sort by most wins.
    const sortedWins = Array.from(totalWinsPerSet).sort(
      (a, b) => b[1].wins - a[1].wins,
    );

    // Adds the winners.
    for (
      let i = 0;
      i < sortedWins.length && i < gameInfo.playerSetsToMoveOn;
      i++
    ) {
      gameSet.winners.push(sortedWins[i][1].playerSet);
    }
    // Adds the losers.
    for (let i = gameInfo.playerSetsToMoveOn; i < sortedWins.length; i++) {
      bracket.losers.push(sortedWins[i][1].playerSet);
    }

    // Update any future options
    if (roundIndex + 1 < bracket.gameSets.length) {
      const nextRound = bracket.gameSets[roundIndex + 1];
      const nextSetIndex = Math.floor(
        gameSetIndex / (gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn),
      );
      if (nextSetIndex < nextRound.length) {
        gameSet.winners.forEach((winner) => {
          nextRound[nextSetIndex].playerSets.push(winner);
        });
      }
    }

    // TODO: Tell the server about the win duh.
  };

  /**
   * Adds a win to the game set for this user. Assumes that the number of winners per game
   * is the same as the number of winners for the set for the game type.
   * @param playerSet The player/team that just got a win.
   */
  const addWin = (gameSet: GameSet, playerSet: PlayerSet) => {
    // Adds to the last game if more than one player/team is meant to move forward.
    const lastGameIndex = gameSet.games.length - 1;
    if (
      lastGameIndex > -1 &&
      gameSet.games[lastGameIndex].winners.length < gameInfo.playerSetsToMoveOn
    ) {
      gameSet.games[lastGameIndex].winners.push(playerSet);
    } else {
      gameSet.games.push({
        id: playerSet.players[0].name, // TODO: What should I do with Ids?
        winners: [playerSet],
        playerSets: gameSet.playerSets,
      });
    }

    // TODO: Send info back to the server about the game result.
    // Patch the last game with the response from the server.
  };

  const removeWin = () => {};

  const revivePlayer = () => {
    // Select a random loser from any previous round.
    // Add them to addedPlayerSets and let the server know.
    // Let Jake exclude people or skip someone.
  };

  /**
   * Gets the number of wins this player/team currently has.
   * @param playerSet The player/team to get wins for.
   * @param gameSet The current gameSet to look in.
   * @returns The number of wins the player/team has.
   */
  const getWins = (gameSet: GameSet, playerSet: PlayerSet): number => {
    let wins = 0;

    gameSet.games.forEach((game) => {
      game.winners.forEach((winner) => {
        if (winner.id === playerSet.id) wins++;
      });
    });

    return wins;
  };

  /**
   * If a button should be disabled.
   * @param gameSet
   */
  const isDisabled = (gameSet: GameSet): boolean => {
    return (
      gameSet.winners.length === gameInfo.playerSetsToMoveOn ||
      gameSet.playerSets.length === 0
    );
  };

  const calcGap = (roundIndex: number): number => {
    // TODO figure this out.
    const startGameSetSize = 4;
    const currentGameSetSize = [4, 2, 1];

    return (
      (startGameSetSize * (2 * minGap + heightCombined) -
        currentGameSetSize[roundIndex] * heightCombined) /
      (2 * currentGameSetSize[roundIndex])
    );
  };

  const calcHeight = (gameSet: GameSet, roundIndex: number): number => {
    // TODO: Simplify
    const gap = calcGap(roundIndex) * 2;
    const adjustedSize = gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn;
    const defaultHeight = 1 - 1 / adjustedSize;
    const heightAdjustment = adjustedSize / gameSet.previousGameSets.length;

    return (defaultHeight / heightAdjustment) * (gap + heightCombined);
  };

  const calcTransform = (gameSet: GameSet): number => {
    const adjustedSize = gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn;
    return (1 / adjustedSize - 1 / gameSet.previousGameSets.length) * 100;
  };

  let offset = 0;
  const snakeCount = (roundIndex: number, gameSetIndex: number): number => {
    if (
      roundIndex + 1 < bracket.gameSets.length &&
      ((gameSetIndex + offset) * gameInfo.playerSetsToMoveOn) /
        gameInfo.maxPlayerSets <
        bracket.gameSets[roundIndex + 1].length
    ) {
      let snakes =
        gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn -
        bracket.gameSets[roundIndex + 1][
          Math.floor(
            ((gameSetIndex + offset) * gameInfo.playerSetsToMoveOn) /
              gameInfo.maxPlayerSets,
          )
        ].previousGameSets.length;
      offset += snakes;
      return snakes;
    }
    return 0;
  };
</script>

<div class="bracket">
  <h1>
    {bracket.gameType
      .toLowerCase()
      .split("_")
      .reduce((prev, curr) => {
        const capPrev = `${prev.charAt(0).toUpperCase()}${prev.substring(1, prev.length)}`;
        const capCurr = `${curr.charAt(0).toUpperCase()}${curr.substring(1, curr.length)}`;
        return `${capPrev} ${capCurr}`;
      })}
  </h1>
  <b>{bracket.date.toDateString()}</b>
  <div class="rounds">
    {#each bracket.gameSets as round, roundIndex}
      <div class="round">
        <h2>Round {roundIndex + 1}</h2>
        <div class="gameSets">
          <!--TODO go through the max number of games possible-->
          {#each round as gameSet, gameSetIndex}
            <div class="gameSet">
              {#if roundIndex !== 0}
                <div
                  class="verticalLine"
                  style={`height: ${calcHeight(gameSet, roundIndex)}px; transform: translateY(${calcTransform(gameSet)}%)`}
                ></div>
                <div class="horizontalLine"></div>
              {/if}
              <div class="set" style={`margin: ${calcGap(roundIndex)}px 0;`}>
                {#each gameSet.playerSets as playerSet}
                  <button
                    aria-label={`Add a win for ${playerSet.name}.`}
                    onclick={() => {
                      addWin(gameSet, playerSet);
                    }}
                    disabled={isDisabled(gameSet)}
                  >
                    <PlayerSetRender {playerSet} gameType={bracket.gameType} />
                    <div>
                      {#each { length: getWins(gameSet, playerSet) }}
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 640 640"
                        >
                          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                          <path
                            d="M64 320C64 178.6 178.6 64 320 64C461.4 64 576 178.6 576 320C576 461.4 461.4 576 320 576C178.6 576 64 461.4 64 320z"
                          />
                        </svg>
                      {/each}
                      {#if gameSet.winners.find((winner) => winner.id === playerSet.id) !== undefined}
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 640 640"
                        >
                          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                          <path
                            d="M345 151.2C354.2 143.9 360 132.6 360 120C360 97.9 342.1 80 320 80C297.9 80 280 97.9 280 120C280 132.6 285.9 143.9 295 151.2L226.6 258.8C216.6 274.5 195.3 278.4 180.4 267.2L120.9 222.7C125.4 216.3 128 208.4 128 200C128 177.9 110.1 160 88 160C65.9 160 48 177.9 48 200C48 221.8 65.5 239.6 87.2 240L119.8 457.5C124.5 488.8 151.4 512 183.1 512L456.9 512C488.6 512 515.5 488.8 520.2 457.5L552.8 240C574.5 239.6 592 221.8 592 200C592 177.9 574.1 160 552 160C529.9 160 512 177.9 512 200C512 208.4 514.6 216.3 519.1 222.7L459.7 267.3C444.8 278.5 423.5 274.6 413.5 258.9L345 151.2z"
                          />
                        </svg>
                      {/if}
                    </div>
                  </button>
                {/each}
                {#each { length: gameInfo.maxPlayerSets - gameSet.playerSets.length }}
                  {#if gameSet.previousGameSets.length === gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn}
                    <button style={`height: ${heightOfSet}px`} disabled>
                      --------
                    </button>
                  {:else}
                    <button
                      aria-label="Revive player."
                      style={`height: ${heightOfSet}px`}
                      disabled={isDisabled(gameSet)}
                      onclick={revivePlayer}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 640 640"
                      >
                        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                        <path
                          d="M311.6 95C297.5 75.5 274.9 64 250.9 64C209.5 64 176 97.5 176 138.9L176 141.3C176 205.7 258 274.7 298.2 304.6C311.2 314.3 328.7 314.3 341.7 304.6C381.9 274.6 463.9 205.7 463.9 141.3L463.9 138.9C463.9 97.5 430.4 64 389 64C365 64 342.4 75.5 328.3 95L320 106.7L311.6 95zM141.3 405.5L98.7 448L64 448C46.3 448 32 462.3 32 480L32 544C32 561.7 46.3 576 64 576L384.5 576C413.5 576 441.8 566.7 465.2 549.5L591.8 456.2C609.6 443.1 613.4 418.1 600.3 400.3C587.2 382.5 562.2 378.7 544.4 391.8L424.6 480L312 480C298.7 480 288 469.3 288 456C288 442.7 298.7 432 312 432L384 432C401.7 432 416 417.7 416 400C416 382.3 401.7 368 384 368L231.8 368C197.9 368 165.3 381.5 141.3 405.5z"
                        />
                      </svg>
                    </button>
                  {/if}
                {/each}
              </div>
              <button
                aria-label="Resolve set."
                onclick={() => {
                  resolveGame(gameSet, roundIndex, gameSetIndex);
                }}
                disabled={isDisabled(gameSet)}
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
                  <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                  <path
                    d="M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z"
                  />
                </svg>
              </button>
              {#if roundIndex !== bracket.gameSets.length - 1}
                <div class="horizontalLine"></div>
              {/if}
            </div>
            {#each { length: snakeCount(roundIndex, gameSetIndex) }}
              <div
                style={`margin: ${calcGap(roundIndex)}px 0; height: ${heightCombined}px`}
              ></div>
            {/each}
          {/each}
        </div>
      </div>
    {:else}
      <p>Loading bracket...</p>
    {/each}
  </div>
</div>

<style>
  .bracket {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    background-color: var(--prime);
    color: var(--text-prime);
    overflow: scroll;
  }

  .rounds {
    display: flex;
    flex-wrap: nowrap;
    overflow-x: auto;
    height: 100%;
  }

  .round {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 100%;
  }

  .gameSets {
    position: relative;
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    align-items: center;
  }

  .gameSet {
    display: flex;
    align-items: center;
    width: 20rem;
  }

  .set {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }

  .set button {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 5px;
    padding: 0 0.5rem;
  }

  .horizontalLine {
    height: 2px;
    width: 2rem;
    background-color: white;
  }

  .verticalLine {
    width: 2px;
    background-color: white;
  }
</style>
