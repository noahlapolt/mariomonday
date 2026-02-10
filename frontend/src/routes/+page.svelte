<!--
    The landing screen will show information about Mario Monday and Smash singles stats.
-->

<script lang="ts">
    import { PUBLIC_API_URL } from "$env/static/public";
    import smashImg from "$lib/assets/Smash.png";
    import { onMount } from "svelte";
    let stats: Player[] = $state([]);

    onMount(() => {
        /* Fetch players */
        const Players_INIT: RequestInit = {
            method: "GET",
        };
        fetch(`${PUBLIC_API_URL}/player`, Players_INIT).then((response) => {
            if (response.status === 200)
                response.json().then((data: Player[]) => {
                    stats = data.sort((player1, player2) => {
                        if (
                            player1.eloMap["SMASH_ULTIMATE_SINGLES"] <
                            player2.eloMap["SMASH_ULTIMATE_SINGLES"]
                        ) {
                            return 1;
                        } else if (
                            player1.eloMap["SMASH_ULTIMATE_SINGLES"] >
                            player2.eloMap["SMASH_ULTIMATE_SINGLES"]
                        ) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                });
            else
                console.log(
                    "There was an error getting the players.",
                    response,
                );
        });
    });
</script>

<div id="landing">
    <div class="contents">
        <h1>Welcome to the Mario Monday Stats Site!</h1>
        <h2>What?</h2>
        <p>
            First you might be thinking: "What is Mario Monday?" Well Mario
            Monday is an event at The Avenue Bar and Grill that happens each
            Monday at 8:30pm. At this event we will play either Mario Kart or
            Super Smash Ultimate. Sometimes it will be a different game if that
            is what the people want.
        </p>
        <h2>Where?</h2>
        <p>
            Did you read the paragraph before? Its at The Avenue Bar and Grill
            (1249 Commonwealth Ave, Allston, MA 02134)
        </p>
        <h2>When?</h2>
        <p>
            Seriously, did you read the first paragraph? Its every Monday at
            8:30pm
        </p>
    </div>
    <div id="leaderBoard" class="contents">
        <img src={smashImg} alt={"of the smash logo."} />
        <div class="leaderBoard">
            {#each [1, 0, 2] as top}
                {#if top < stats.length}
                    <div
                        class="leader"
                        style={`height: ${["75%", "50%", "25%"][top]};`}
                    >
                        {top + 1}
                        <p class="user">{stats[top].name}</p>
                    </div>
                {/if}
            {/each}
        </div>
        <a href={`selectgame`}>New Tournament</a>
    </div>
</div>

<style>
    #landing {
        display: flex;
        flex-wrap: wrap;
        width: calc(100vw - 2rem);
        padding: 1rem;
        justify-content: center;
        background-color: var(--prime);
        color: var(--text-prime);
    }

    #leaderBoard {
        justify-content: space-between;
    }

    #leaderBoard img {
        max-height: 30%;
        object-fit: contain;
    }

    .contents {
        height: calc(100vh - 5rem);
        display: flex;
        flex-direction: column;
        flex-grow: 1;
        text-align: center;
        min-width: 50%;
        max-width: 20rem;
    }

    .leaderBoard {
        display: flex;
        justify-content: center;
        align-items: end;
        gap: 1rem;
        flex-grow: 1;
        max-height: 40%;
    }

    .leader {
        background-color: #999999;
        position: relative;
        width: calc(33% - 2rem);
        text-align: center;
        font-size: xx-large;
        padding: 1rem;
    }

    .user {
        position: absolute;
        top: -4rem;
        left: 0rem;
        width: 100%;
        text-align: center;
        color: #999999;
    }
</style>
