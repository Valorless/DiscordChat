# DiscordBans
<a href="https://github.com/Valorless/DiscordChatMonitor" rel="nofollow"><img src="https://img.shields.io/badge/Versions-1.18%20--%201.20-brightgreen?style=flat" alt="Versions" style="max-width: 100%;"/>
<a href="https://github.com/Valorless/ValorlessUtils" rel="nofollow"><img src="https://img.shields.io/badge/Requires-ValorlessUtils-red?style=flat" alt="Dependency" style="max-width: 100%;"/>
<br>

DB catches the following commands and sends it to discord via webhook.
| Command | From |
| --- | --- |
| `/ban` | Minecraft and Essentials |
| `/tempban` | Essentials |
| `/banip` | Essentials |
| `/tempban` | Essentials |
| `/unban` | Essentials |
| `/unban-ip` | Essentials |
| `/pardon` | Minecraft |
| `/pardon-ip` | Minecraft |

Once a command is caught, information regarding the target, sender, reason and more, is sent to the webhook.

*Currently DiscordBans does not catch commands cast by the console.*

## Commands
| Command | Description |
| --- | --- |
| `/db reload` | Reloads config.yml |
| `/db disable` | Disabled the plugin. Requires a reload or restart to re-enable. |
| `/db debug` | Enable/Disable debugging for DiscordBans. |
  
## Permissions
| Permission | Description |
| --- | --- |
| `discordbans.*` | Gives all DiscordBans permissions. |
| `discordbans.reload` | Allows usage of /db reload. |
| `discordbans.disable` | Allows you to disable the plugin. |
| `discordbans.debug` | Allows you to enable/disable debugging. |

## Messages

Within /plugins/DiscordBans/config.yml you'll find a 'message' section. You can customize the messages to anything you want, with or without provided placeholders:
| Placeholder | Description |
| --- | --- |
| `%target%` | Target of the command. |
| `%sender%` | Sender of the command. |
| `%reason%` | Reason for the ban. |
| `%duration%` | Duration of the tempban. |
| `%date%` | Server time when the ban occured. |
| `%plugin%` | Will always return '&7[&4DiscordBans&9]&r'. |

## Configuration
| Config Entry | Description | Default | Comment |
| --- | :---: | :---: | :---: |
| `webhook-url` | Webhook URL found in the integrations section of a channel's settings. |  | If left blank, the plugin will disable itself until it has been set. |
| `bot-name` | Name of the Webhook sending the message. | George | Leave blank to use the one specified in the integration section. |
| `bot-picture` | Profile Picture of the Webhook. Must be a URL. | [Image](https://i.pinimg.com/originals/bf/23/ca/bf23ca87c2a867e2b3b991e76d982abd.jpg) | Leave blank to use the one specified in the integration section. |
| `ban-color` | Color of the embed when a player is banned. | #ff2b2b | Hex color, Red. |
| `tempban-color` | Color of the embed when a player is temp banned. | #ff992b | Hex color, Orange. |
| `unban-color` | Color of the embed when a player is unbanned. | #2afa4d | Hex color, Green. |
| `banip-color` | Color of the embed when a player is ip banned. | #5b09ad | Hex color, Purple. |
| `unbanip-color` | Color of the embed when a player is ip unbanned. | #0ce6fa | Hex color, Light Blue. |
| `bans` | Whether the plugin should send bans to the discord server or not. | true |  |
| `tempbans` | Whether the plugin should send temp bans to the discord server or not. | true |  |
| `unbans` | Whether the plugin should send unbans to the discord server or not. | true |  |
| `banips` | Whether the plugin should send ip-bans to the discord server or not. | true |  |
| `unbanips` | Whether the plugin should send ip-unbans to the discord server or not. | true |  |
| `debug` | Enabling 'debug' will make the plugin send additional messages in console. | false |  |
