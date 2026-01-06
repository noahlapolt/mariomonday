
export const GameTypes: Record<string, GameType> = {
    MARIO_KART_WORLD: {
        maxPlayerSets: 4,
        playerSetsToMoveOn: 2,
        playersOnATeam: 1,
    },
    MARIO_KART_8: {
        maxPlayerSets: 4,
        playerSetsToMoveOn: 2,
        playersOnATeam: 1,
    },
    SMASH_ULTIMATE_SINGLES: {
        maxPlayerSets: 2,
        playerSetsToMoveOn: 1,
        playersOnATeam: 1,
    },
    SMASH_ULTIMATE_DOUBLES: {
        maxPlayerSets: 2,
        playerSetsToMoveOn: 1,
        playersOnATeam: 2,
    },
};

export const globalStates: {
    login: boolean;
} = $state({
    login: false,
});
