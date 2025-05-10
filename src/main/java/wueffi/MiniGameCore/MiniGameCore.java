package wueffi.MiniGameCore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wueffi.MiniGameCore.commands.MiniGameCommand;
import wueffi.MiniGameCore.managers.GameManager;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.managers.ScoreBoardManager;
import wueffi.MiniGameCore.utils.*;


import java.util.List;

public class MiniGameCore extends JavaPlugin {
    private List<String> availableGames;
    private List<String> bannedPlayers;
    private final MiniGameCore plugin = this;

    @Override
    public void onEnable() {
        getLogger().info("MinigameCore enabled!");
        saveDefaultConfig();

        List<String> availableGames = getConfig().getStringList("available-games");
        List<String> bannedPlayers = getConfig().getStringList("banned-players");
        this.availableGames = availableGames;
        this.bannedPlayers = bannedPlayers;
        getLogger().info("Config loaded!");

        Stats.setup();
        getLogger().info("Stats loaded!");

        getCommand("mg").setExecutor(new MiniGameCommand(this));
        getCommand("mg").setTabCompleter(new MiniGameTabCompleter(this));
        getLogger().info("Commands registered!");

        ScoreBoardManager.startAnimationLoop();

        Bukkit.getPluginManager().registerEvents(new GameManager(this), this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
    }

    @Override
    public void onDisable() {
        for (Lobby lobby : LobbyManager.getInstance().getOpenLobbies()) {
            String lobbyid = lobby.getLobbyId();
            getLogger().info("Lobby: " + lobbyid);
            for (Player player : LobbyManager.getInstance().getLobby(lobbyid).getPlayers()) {
                getLogger().info("Player: " + lobbyid);
                PlayerHandler.PlayerReset(player);
            }
            getLogger().info("Lobby disabling: " + lobbyid);
            LobbyHandler.LobbyReset(LobbyManager.getInstance().getLobby(lobbyid));
            getLogger().info("Shut down Lobby: " + lobbyid);
        }
        getLogger().info("MinigameCore disabled!");
    }

    public List<String> getAvailableGames() {
        return availableGames;
    }
    public List<String> getBannedPlayers() {
        return bannedPlayers;
    }
    public void banPlayer(String player){
        bannedPlayers.add(player);
        getConfig().set("banned-players", bannedPlayers);
        saveConfig();
    }
    public void unbanPlayer(String player){
        bannedPlayers.remove(player);
        getConfig().set("banned-players", bannedPlayers);
        saveConfig();
    }
    public MiniGameCore getPlugin() {
        return plugin;
    }
}
