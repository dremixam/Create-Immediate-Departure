[![Modrinth Downloads](https://img.shields.io/modrinth/dt/create-immediate-departure)](https://modrinth.com/mod/create-immediate-departure)
![GitHub Release Date](https://img.shields.io/github/release-date/dremixam/Create-Immediate-Departure)
[![Discord](https://discordapp.com/api/guilds/745755550180048906/widget.png?style=shield)](https://discord.dremixam.com)
[![Twitch Status](https://img.shields.io/twitch/status/dremixam)](https://twitch.tv/dremixam)

# Create: Immediate Departure

Minecraft mod for [Create](https://modrinth.com/mod/create) that adds fast travel between the
mod's train stations. Works on Fabric and NeoForge.

## The idea

Trains in Create take a while, and that's exactly why a lot of servers reach for Waystones or
similar teleport mods, which let players skip trains, and the rail network, entirely. Immediate
Departure is meant to replace that: a direct teleport between two stations that still requires you
to have actually built the railway, instead of one that makes building it pointless. As long as
you've genuinely earned the shortcut:

- **Discovery** you must have physically been near the destination station at least once. No
  teleporting to a station you've never visited.
- **Rail connection** origin and destination must belong to the same connected rail network. Two
  stations with no track linking them, even if both discovered, aren't offered as destinations for
  each other.
- **Active schedule** (optional, enabled by default) a running Create Schedule must actually
  serve both stations. Can be turned off in the config if you'd rather ignore this condition.

There's no extra cost or cooldown: the cost is having built the network and made the trip once.

## Usage

- The **ticket** button added directly to Create's own station screen.
- Right-click a placed **Ticket Validator** a block that opens a linked station's destination
  list without needing to stand at the station itself. Right-click a station with the Ticket
  Validator item to select it, then place the block anywhere within range to link it; an in-game
  Ponder tutorial (right-click the item, or check its tooltip) walks through the whole thing.

The list only shows stations you've discovered and can currently reach from that one. Clicking a
destination teleports you there right away, next to the station (not in the middle of the tracks).
Newly discovering a station shows a confirmation toast.

## Configuration

An in-game config screen is available from the Mods menu (ModMenu on Fabric, NeoForge's native
mod list on NeoForge). It's backed by `create_immediate_departure.json` (in the instance's
`config/` folder), created automatically on first launch:

| Key | Default | Effect                                                                                         |
|---|---|------------------------------------------------------------------------------------------------|
| `discoveryRadius` | `32.0` | Distance (in blocks) from the train_station block at which it gets marked as discovered.      |
| `requireActiveSchedule` | `true` | If `true`, an active Schedule must serve both stations in addition to the rail connection.     |
| `ticketValidatorRange` | `28.0` | Maximum distance (in blocks) a Ticket Validator can be placed from the station it's linked to. |

Changes made in-game apply as soon as you save the screen; editing the JSON file directly requires
a restart.

## Supported versions

| Minecraft | Loader | Create |
|---|---|--------|
| 1.20.1 | Fabric | 6.x    |
| 1.21.1 | NeoForge | 6.x    |

Requires Create, plus [Cloth Config](https://modrinth.com/mod/cloth-config) for the in-game config
screen ([ModMenu](https://modrinth.com/mod/modmenu) too on Fabric), all declared as hard
dependencies in the mod's metadata.


