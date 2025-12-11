<script lang="ts">
    import BracketRender from "$lib/components/BracketRender.svelte";
    import PlayerSetRender from "$lib/components/PlayerSetRender.svelte";

    /* The different teams to render */
    let testTeam1: PlayerSet = { id: "", name: "Team 1", players: [] };
    let testTeam2: PlayerSet = {
        id: "",
        name: "Team 2",
        players: [
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
        ],
    };
    let testTeam3: PlayerSet = {
        id: "",
        name: "Team 3",
        players: [
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
        ],
    };
    let testTeam4: PlayerSet = {
        id: "",
        name: "Team 3",
        players: [
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
        ],
    };
    let testTeam5: PlayerSet = {
        id: "",
        name: "Team 3",
        players: [
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
            { id: "", name: "Noah", eloMap: { SMASH_ULTIMATE_SINGLES: 1500 } },
        ],
    };
</script>

<div class="test">
    <!-- See player set render with singles. -->
    <h1>All Tests</h1>
    <h2>Player Set Render</h2>
    <h3>SMASH_ULTIMATE_SINGLES</h3>
    <h4>No Players</h4>
    <PlayerSetRender gameType="SMASH_ULTIMATE_SINGLES" playerSet={testTeam1} />
    <h4>1 Player</h4>
    <PlayerSetRender gameType="SMASH_ULTIMATE_SINGLES" playerSet={testTeam2} />
    <h3>SMASH_ULTIMATE_DOUBLES</h3>
    <h4>No Players</h4>
    <PlayerSetRender gameType="SMASH_ULTIMATE_DOUBLES" playerSet={testTeam1} />
    <h4>1 Player</h4>
    <PlayerSetRender gameType="SMASH_ULTIMATE_DOUBLES" playerSet={testTeam2} />
    <h4>2 Players</h4>
    <PlayerSetRender gameType="SMASH_ULTIMATE_DOUBLES" playerSet={testTeam3} />
    <h3>MARIO_KART_WORLD</h3>
    <h4>No Players</h4>
    <PlayerSetRender gameType="MARIO_KART_WORLD" playerSet={testTeam1} />
    <h4>1 Player</h4>
    <PlayerSetRender gameType="MARIO_KART_WORLD" playerSet={testTeam2} />
    <h4>2 Players</h4>
    <PlayerSetRender gameType="MARIO_KART_WORLD" playerSet={testTeam3} />
    <h4>3 Players</h4>
    <PlayerSetRender gameType="MARIO_KART_WORLD" playerSet={testTeam4} />
    <h4>4 Players</h4>
    <PlayerSetRender gameType="MARIO_KART_WORLD" playerSet={testTeam5} />
    <h2>Bracket Render</h2>
    <h3>SMASH_ULTIMATE_SINGLES</h3>
    <h4>No Rounds</h4>
    <BracketRender bracketId="0" />
    <h4>1 Round 0 Byes</h4>
    <BracketRender bracketId="1" />
    <h4>2 Rounds 0 Byes</h4>
    <BracketRender bracketId="2" />
    <h4>2 Rounds 1 Bye</h4>
    <BracketRender bracketId="3" />
    <h4>3 Rounds 0 Byes</h4>
    <BracketRender bracketId="4" />
    <h4>3 Rounds 1 Bye</h4>
    <BracketRender bracketId="5" />
    <h4>3 Rounds 2 Byes</h4>
    <BracketRender bracketId="6" />
    <h3>MARIO_KART_8</h3>
    <h4>No Rounds</h4>
    <BracketRender bracketId="7" />
    <h4>1 Round 0 Byes</h4>
    <BracketRender bracketId="8" />
    <h4>2 Rounds 0 Byes</h4>
    <BracketRender bracketId="9" />
    <h4>2 Rounds 1 Bye</h4>
    <BracketRender bracketId="10" />
    <h4>3 Rounds 0 Byes</h4>
    <BracketRender bracketId="11" />
    <h4>3 Rounds 1 Bye</h4>
    <BracketRender bracketId="12" />
    <h4>3 Rounds 2 Byes</h4>
    <BracketRender bracketId="13" />
    <h3>SMASH_ULTIMATE_DOUBLES</h3>
    <h4>No Rounds</h4>
    <BracketRender bracketId="14" />
    <h4>1 Round 0 Byes</h4>
    <BracketRender bracketId="15" />
    <h4>2 Rounds 0 Byes</h4>
    <BracketRender bracketId="16" />
    <h4>2 Rounds 1 Bye</h4>
    <BracketRender bracketId="17" />
    <h4>3 Rounds 0 Byes</h4>
    <BracketRender bracketId="18" />
    <h4>3 Rounds 1 Bye</h4>
    <BracketRender bracketId="19" />
    <h4>3 Rounds 2 Byes</h4>
    <BracketRender bracketId="20" />
</div>

<style>
    .test {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
</style>
