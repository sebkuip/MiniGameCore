package wueffi.MiniGameCore.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class LuckPermsUtil {
    public static boolean hasPermission(Player player, String permission) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        return user != null && user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}