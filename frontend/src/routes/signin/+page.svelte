<script lang="ts">
    import { PUBLIC_API_URL } from "$env/static/public";

    let username = $state("");
    let password = $state("");
</script>

<h1>Sign In</h1>
<label>
    Username:
    <input bind:value={username} type="text" />
</label>
<label>
    Password:
    <input bind:value={password} type="password" />
</label>
<button
    onclick={() => {
        const login_INIT: RequestInit = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization:
                    "Basic " +
                    new TextEncoder()
                        .encode(username + ":" + password)
                        .toBase64(),
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        };
        fetch(`${PUBLIC_API_URL}/login`, login_INIT).catch((e) => {
            console.log("There was an error logging you in.", e);
        });
    }}
>
    Submit
</button>
