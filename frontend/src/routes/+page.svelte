<!--
    The landing screen is meant to show the next closest game and it's stats on mobile or two different games
    and stats on desktop.
-->

<script lang="ts">
    const games = {
        smash: {
            style: "background-color: #0c0c0c",
            img: "",
            alt: "of the smash logo.",
        },
        kart: {
            style: "background-color: #47ffe3",
            img: "",
            alt: "of the kart logo.",
        },
    };
    let desktop = $state(true); //TODO calculate this value based on the width of the screen.
    let nextGame = $state(games.smash); //TODO figure out how to calculate this value.
    let stats = [
        { name: "Noah", win: 5, loss: 100 },
        { name: "Noah", win: 5, loss: 100 },
        { name: "Noah", win: 5, loss: 100 },
        { name: "Noah", win: 5, loss: 100 },
    ]; //api.getStats();
</script>

<div id="landing">
    {#each [games.smash, games.kart] as game}
        {#if nextGame === game || desktop}
            <div class="background" style={game.style}>
                <div class="startScreen">
                    <img src={game.img} alt={game.alt} />
                    <div>
                        <!--Add the leaderboard stack-->
                    </div>
                    <button>Start Tournament</button>
                </div>
                <div class="stats">
                    {#each stats as stat}
                        <div class="stat">
                            <!--TODO figure out if a plural is needed here-->
                            <span>Player: {stat.name}</span>
                            <span>Wins: {stat.win}</span>
                            <span>Losses: {stat.loss}</span>
                            <span>Ratio: {stat.win / stat.loss}</span>
                        </div>
                    {/each}
                </div>
            </div>
        {/if}
    {/each}
</div>

<style>
    #landing {
        display: flex;
        flex-wrap: wrap;
        width: 100vw;
    }

    .background {
        padding: 2rem;
        flex-grow: 1;
    }

    .startScreen {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        height: 100vh;
    }

    .stats {
        display: flex;
        flex-direction: column;
    }

    .stat {
        display: flex;
        justify-content: space-around;
        padding: 0.5rem 0;
        margin: 0.5rem 0;
        width: 100%;
        background-color: #999999;
    }
</style>
