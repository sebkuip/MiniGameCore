package wueffi.MiniGameCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wueffi.MiniGameCore.MiniGameCore;
import wueffi.MiniGameCore.managers.GameManager;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.managers.ScoreBoardManager;
import wueffi.MiniGameCore.utils.*;

import java.util.Arrays;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class MiniGameCommand implements CommandExecutor {
    private final MiniGameCore plugin;

    public MiniGameCommand(MiniGameCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        LobbyManager lobbyManager = LobbyManager.getInstance();
        Lobby lobby;

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Yo console User, only players can use this command!");
            return true;
        }

        String[] commands = {"host", "join", "leave", "start", "spectate", "reload", "stopall", "stop", "ban", "unban"};
        String[] permissions = {
                "mgcore.host", "mgcore.join", "mgcore.leave", "mgcore.start", "mgcore.spectate",
                "mgcore.admin", "mgcore.admin", "mgcore.admin", "mgcore.admin", "mgcore.admin"
        };

        if (args.length < 1) {
            StringBuilder availableCommands = new StringBuilder("§fUsage: §6/mg <");

            for (int i = 0; i < commands.length; i++) {
                if (LuckPermsUtil.hasPermission(player, permissions[i])) {
                    availableCommands.append(commands[i]).append(" | ");
                }
            }
            if (availableCommands.length() > 0) {
                availableCommands.setLength(availableCommands.length() - 3);
            }
            availableCommands.append(">");
            player.sendMessage(availableCommands.toString());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "host":
                if (plugin.getBannedPlayers().contains(player.getUniqueId())) {
                    player.sendMessage("§cYou were banned by an Administrator.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg host <game>");
                    return true;
                }
                if (!LuckPermsUtil.hasPermission(player, "mgcore.host")) {
                    player.sendMessage("§cYou have no permissions to use this Command!");
                    return true;
                }
                if (LobbyManager.getLobbyByPlayer(player) != null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cYou are already in another lobby!");
                    return true;
                }
                String gameName = args[1];
                if (!plugin.getAvailableGames().contains(gameName)) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cGame " + gameName + " not available!");
                    return true;
                }
                GameManager gameManager = new GameManager(plugin);
                gameManager.hostGame(gameName, sender);
                player.sendMessage("§8[§6MiniGameCore§8]§a Hosting game: " + args[1]);
                ScoreBoardManager.setPlayerStatus(player, "WAITING");
                break;

            case "join":
                if (plugin.getBannedPlayers().contains(player.getUniqueId())) {
                    player.sendMessage("§cYou were banned by an Administrator.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg join <game>");
                    return true;
                }

                String lobbyName = args[1];
                lobby = lobbyManager.getLobby(lobbyName);

                if (LobbyManager.getLobbyByPlayer(player) != null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cYou are already in another lobby!");
                    return true;
                }

                if (!Objects.equals(lobby.getLobbyState(), "WAITING")) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cThe game already started!");
                    return true;
                }
                if (lobby == null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cLobby not found!");
                    return true;
                }

                if (lobby.isFull()) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cLobby is already full!");
                    return true;
                }

                if (!lobby.addPlayer(player)) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cCould not join the lobby.");
                    return true;
                }
                for (Player gamer : lobby.getPlayers()) {
                    gamer.sendMessage("§8[§6MiniGameCore§8]§a " + player.getName() + " joined! " +
                            lobby.getPlayers().size() + "/" + lobby.getMaxPlayers() + " players.");
                    player.teleport(lobby.getOwner());
                    player.setGameMode(GameMode.SURVIVAL);
                    ScoreBoardManager.setPlayerStatus(player, "WAITING");
                }
                if (lobby.isFull()) {
                    GameManager.startGame(lobby);
                }
                break;

            case "leave":
                if (args.length >= 2) {
                    player.sendMessage("§cToo many Args! Usage: /mg leave");
                    return true;
                }

                lobby = LobbyManager.getLobbyByPlayer(player);

                if (lobby == null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cYou are not in any lobby!");
                    return true;
                }

                if (lobby.removePlayer(player)) {
                    PlayerHandler.PlayerReset(player);
                    for (Player gamer : lobby.getPlayers()) {
                        gamer.sendMessage("§8[§6MiniGameCore§8]§a " + player.getName() + " left the Lobby! " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers() + " players.");
                    }

                    if (lobby.isOwner(player)) {
                        player.sendMessage("§8[§6MiniGameCore§8] §cYou were the owner of this lobby. The game will now be stopped.");
                        for (Player gamer : lobby.getPlayers()) {
                            gamer.sendMessage("§8[§6MiniGameCore§8]§c Lobby Owner " + player.getName() + " left the Lobby! Resetting...");
                            PlayerHandler.PlayerReset(gamer);
                        }
                        LobbyHandler.LobbyReset(lobby);
                    }
                    ScoreBoardManager.setPlayerStatus(player, "NONE");
                } else {
                    player.sendMessage("§8[§6MiniGameCore§8] §cFailed to leave the game. Please try again.");
                }
                break;


            case "start":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.start")) {
                    player.sendMessage("§cYou have no permissions to use this command!");
                    return true;
                }

                lobby = LobbyManager.getLobbyByPlayer(player);
                if (lobby == null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cYou are not in a lobby!");
                    return true;
                }

                if (!lobby.isOwner(player)) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cOnly the lobby owner can start the game!");
                    return true;
                }
                GameManager.startGame(lobby);
                player.sendMessage("§8[§6MiniGameCore§8] §aStarting game: " + lobby.getLobbyId());
                break;

            case "spectate":
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg spectate <game|player>");
                    return true;
                }
                if (!LuckPermsUtil.hasPermission(player, "mgcore.spectate")) {
                    player.sendMessage("§cYou have no permissions to use this Command!");
                    return true;
                }
                if (LobbyManager.getLobbyByPlayer(player) != null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cYou are already in a game! Type /mg leave to leave!");
                    return true;
                }

                String target = args[1];

                Player targetPlayer = Bukkit.getPlayer(target);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    player.sendMessage("§8[§6MiniGameCore§8] §aYou are now spectating " + targetPlayer.getName() + ".");
                    player.teleport(targetPlayer);
                } else {
                    lobby = LobbyManager.getInstance().getLobby(target);
                    if (lobby != null) {
                        player.sendMessage("§8[§6MiniGameCore§8] §aYou are now spectating the lobby of " + lobby.getOwner().getName() + ".");
                        player.teleport(lobby.getOwner());
                    } else {
                        player.sendMessage("§8[§6MiniGameCore§8]§cNo player or lobby found with that name.");
                    }
                }
                break;

            case "stats":
                if (args.length == 1) {
                    player.sendMessage("§cBenutzung: /mg stats <Spieler>");
                    return true;
                }

                OfflinePlayer targetplayer = Bukkit.getOfflinePlayer(args[1]);

                player.sendMessage("§8[§6MiniGameCore§8] §6Stats for " + targetplayer.getName() + ":");

                for (String game : plugin.getAvailableGames()) {
                    int played = Stats.getPlayed(game, targetplayer);
                    int wins = Stats.getWins(game, targetplayer);
                    int losses = Stats.getLosses(game, targetplayer);
                    float winrate = 0;

                    if (played > 0 || wins > 0 || losses > 0) {
                        if (wins > 0) {
                            winrate = ((float) wins / played) * 100;
                            winrate = Math.round(winrate * 10) / 10.0f;
                        }
                        player.sendMessage("§7- §a" + game + "§7: §f" + played + " §agames played, §6" + wins + " §agames won, §c" + losses + " §alost. Win rate: §3" + winrate + "§a%");
                    }
                }
                break;

            case "reload":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                    player.sendMessage("§cYou have no permissions to use this Command!");
                    return true;
                }
                plugin.reloadConfig();
                Stats.setup();
                player.sendMessage("§8[§6MiniGameCore§8] §aPlugin reloaded!");
                break;

            case "stopall":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                    player.sendMessage("§cYou have no permissions to use this Command!");
                    return true;
                }
                if (lobbyManager.getOpenLobbies() == null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cNo active Lobbies.");
                    return true;
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cStopping all games!");
                for (Lobby lobby1 : LobbyManager.getInstance().getOpenLobbies()) {
                    for (Player gamer : lobby1.getPlayers()) {
                        gamer.sendMessage("§8[§6MiniGameCore§8]§c Administrator stopped the game! Resetting...");
                        PlayerHandler.PlayerReset(gamer);
                        LobbyHandler.LobbyReset(lobby1);
                    }
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cStopped all games.");
                break;

            case "stop":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                    player.sendMessage("§cNo permission!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg stop <game>");
                    return true;
                }
                if (lobbyManager.getLobby(args[1]) == null) {
                    player.sendMessage("§8[§6MiniGameCore§8] §cNo active Lobbies.");
                    return true;
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cStopping game: " + args[1]);
                lobby = lobbyManager.getLobby(args[1]);
                for (Player gamer : lobby.getPlayers()) {
                    gamer.sendMessage("§8[§6MiniGameCore§8]§c Administrator stopped the game! Resetting...");
                    PlayerHandler.PlayerReset(gamer);
                    LobbyHandler.LobbyReset(lobby);
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cStopped game: " + args[1]);
                break;

            case "ban":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                    player.sendMessage("§cYou don't have permissions to use this Command!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg ban <player>");
                    return true;
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cBanning player: " + args[1]);
                plugin.banPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
                if (args.length == 2) {
                    getLogger().info(player.getName() + " banned Player: " + args[1] + ".");
                } else {
                    String[] tempReason = Arrays.copyOfRange(args, 2, args.length);
                    String reason = String.join(" ", tempReason);
                    getLogger().info(player.getName() + " banned Player: " + args[1] + "with reason: " + reason);
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cBanned player: " + args[1]);
                break;

            case "unban":
                if (!LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                    player.sendMessage("§cYou don't have permissions to use this Command!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cMissing Args! Usage: /mg unban <player>");
                    return true;
                }
                player.sendMessage("§8[§6MiniGameCore§8] §cUnbanning player: " + args[1]);
                plugin.unbanPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
                getLogger().info(player.getName() + " unbanned Player: " + args[1] + ".");
                player.sendMessage("§8[§6MiniGameCore§8] §cUnbanned player: " + args[1]);
                break;

            default:
                player.sendMessage("§8[§6MiniGameCore§8] §cUnknown subcommand!");
                break;
        }

        return true;
    }
}
