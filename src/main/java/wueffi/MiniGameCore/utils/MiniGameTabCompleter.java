package wueffi.MiniGameCore.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wueffi.MiniGameCore.MiniGameCore;
import wueffi.MiniGameCore.managers.LobbyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiniGameTabCompleter implements TabCompleter {
    private final MiniGameCore plugin;

    public MiniGameTabCompleter(MiniGameCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();
        LobbyManager lobbyManager = LobbyManager.getInstance();

        String[] commands = {"host", "join", "leave", "start", "spectate", "stats", "reload", "stopall", "stop", "ban", "unban"};
        String[] permissions = {
                "mgcore.host", "mgcore.join", "mgcore.leave", "mgcore.start", "mgcore.spectate", "mgcore.stats",
                "mgcore.admin", "mgcore.admin", "mgcore.admin", "mgcore.admin", "mgcore.admin"
        };

        if (args.length == 1) {
            for (int i = 0; i < commands.length; i++) {
                if (LuckPermsUtil.hasPermission(player, permissions[i])) {
                    completions.add(commands[i]);
                }
            }
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "host":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.host")) {
                        if (!plugin.getBannedPlayers().contains(player.getName())) {
                            completions = plugin.getAvailableGames();
                        }
                    }
                    break;
                case "join":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.join")) {
                        if (!plugin.getBannedPlayers().contains(player.getName())) {
                            completions = new ArrayList<>();
                            for (Lobby lobby : LobbyManager.getInstance().getOpenLobbies()) {
                                String lobbyId = lobby.getLobbyId();
                                completions.add(lobbyId);
                            }
                        }
                    }
                    break;
                case "spectate":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.spectate")) {
                        completions = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                        for (Lobby lobby : LobbyManager.getInstance().getOpenLobbies()) {
                            String lobbyId = lobby.getLobbyId();
                            completions.add(lobbyId);
                        }
                    }
                    break;
                case "stats":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.stats")) {
                        completions = Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .collect(Collectors.toList());
                    }
                    break;
                case "stop":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                        completions = new ArrayList<>();
                        for (Lobby lobby : LobbyManager.getInstance().getOpenLobbies()) {
                            String lobbyId = lobby.getLobbyId();
                            completions.add(lobbyId);
                        }
                    }
                    break;
                case "ban", "unban":
                    if (LuckPermsUtil.hasPermission(player, "mgcore.admin")) {
                        completions = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                    }
                    break;
            }
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
