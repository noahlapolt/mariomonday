<script lang="ts">
    import { SvelteMap } from "svelte/reactivity";
    import { GameTypes } from "./Utils.svelte";
    import PlayerSetRender from "./PlayerSetRender.svelte";
    import snake from "$lib/assets/snake.png";

    let {
        bracket,
    }: {
        bracket: Bracket;
    } = $props();

    // Get some important round info.
    let playerSetMap: SvelteMap<string, PlayerSet> = $derived.by(() => {
        const map = new SvelteMap<string, PlayerSet>();

        // Makes a map of the teams for quicker refrence.
        // TODO: Ask for the backend to send it like this.
        bracket.teams.forEach((team) => {
            map.set(team.id, team);
        });

        return map;
    });

    const gameInfo = GameTypes[bracket.gameType];
    const maxPrevGames = gameInfo.maxPlayerSets / gameInfo.playerSetsToMoveOn;
    const playerSetHeight =
        gameInfo.playersOnATeam * 40 + (gameInfo.playersOnATeam > 1 ? 30 : 0);
    const gameSetsHeight =
        (playerSetHeight + 12) *
            bracket.gameSets[0].length *
            gameInfo.maxPlayerSets +
        4;
    const MEDALS = ["#FFD700", "#C0C0C0", "#CD7F32", "#000000"];
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
    <b>{new Date(bracket.date).toDateString()}</b>
    <div class="rounds">
        {#each bracket.gameSets as gameSets, roundIndex}
            <div class="round">
                <h2>Round {roundIndex + 1}</h2>
                <div class="gameSets" style={`height: ${gameSetsHeight}px`}>
                    {#each gameSets as gameSet, gameSetIndex}
                        <div class="gameSetWrapper">
                            {#if roundIndex !== 0}
                                <div class="vertLine"></div>
                            {/if}
                            <!-- Display a GameSet -->
                            <div class="gameSet">
                                {#each gameSet.playerSets as playerSetId}
                                    <div class="playerSet">
                                        <PlayerSetRender
                                            playerSet={playerSetMap.get(
                                                playerSetId,
                                            )}
                                            gameType={bracket.gameType}
                                        />
                                        <div>
                                            {#each gameSet.games as game}
                                                {#each game.playerSets as playerSet, index}
                                                    {#if playerSet.id === playerSetId}
                                                        <svg
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            viewBox="0 0 640 640"
                                                        >
                                                            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                                                            <path
                                                                fill={MEDALS[
                                                                    index
                                                                ]}
                                                                d="M64 320C64 178.6 178.6 64 320 64C461.4 64 576 178.6 576 320C576 461.4 461.4 576 320 576C178.6 576 64 461.4 64 320z"
                                                            />
                                                        </svg>
                                                    {/if}
                                                {/each}
                                            {/each}
                                            {#each gameSet.winners as winner}
                                                {#if winner === playerSetId}
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
                                            {/each}
                                        </div>
                                    </div>
                                {/each}
                                {#each { length: gameInfo.maxPlayerSets - gameSet.playerSets.length }}
                                    <div
                                        class="invis"
                                        style={`height: ${playerSetHeight}px;`}
                                    >
                                        <img
                                            src={snake}
                                            alt="of and you can't even say, my name (Snake)"
                                        />
                                    </div>
                                {/each}
                            </div>
                            <!-- Display the correct line. -->
                            {#if roundIndex !== bracket.gameSets.length - 1}
                                <div class="vertLine"></div>
                                {#if gameSetIndex % maxPrevGames === 0}
                                    <div
                                        class="horLine"
                                        style="height: 50%; transform: translateY(50%)"
                                    ></div>
                                {:else if gameSetIndex % maxPrevGames === maxPrevGames - 1}
                                    <div
                                        class="horLine"
                                        style="height: 50%; transform: translateY(-50%)"
                                    ></div>
                                {:else}
                                    <div
                                        class="horLine"
                                        style="height: 100%;"
                                    ></div>
                                {/if}
                            {/if}
                        </div>
                    {/each}
                </div>
            </div>
        {/each}
    </div>
</div>

<style>
    .bracket {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: var(--prime);
        color: var(--text-prime);
    }

    .rounds {
        display: flex;
        flex-wrap: nowrap;
        max-width: 100vw;
        overflow: auto;
    }

    .round {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .gameSets {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
    }

    .gameSetWrapper {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-grow: 1;
    }

    .horLine {
        background-color: aliceblue;
        width: 2px;
    }

    .vertLine {
        background-color: aliceblue;
        height: 2px;
        width: 2rem;
    }

    .gameSet {
        width: 20rem;
        border-top: white 1px solid;
        border-bottom: white 1px solid;
        background-color: var(--secondary);
    }

    .playerSet {
        justify-content: space-between;
        flex-grow: 1;
    }

    .invis img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }
</style>
