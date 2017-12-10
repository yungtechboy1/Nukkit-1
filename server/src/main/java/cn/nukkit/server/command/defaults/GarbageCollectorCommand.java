package cn.nukkit.server.command.defaults;

import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.NukkitMath;
import cn.nukkit.server.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class GarbageCollectorCommand extends VanillaCommand {

    public GarbageCollectorCommand(String name) {
        super(name, "%nukkit.command.gc.description", "%nukkit.command.gc.usage");
        this.setPermission("nukkit.command.gc");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        int chunksCollected = 0;
        int entitiesCollected = 0;
        int tilesCollected = 0;
        long memory = Runtime.getRuntime().freeMemory();

        for (Level level : sender.getServer().getLevels().values()) {
            int chunksCount = level.getChunks().size();
            int entitiesCount = level.getEntities().length;
            int tilesCount = level.getBlockEntities().size();
            level.doChunkGarbageCollection();
            level.unloadChunks(true);
            chunksCollected += chunksCount - level.getChunks().size();
            entitiesCollected += entitiesCount - level.getEntities().length;
            tilesCollected += tilesCount - level.getBlockEntities().size();
            level.clearCache(true);
        }

        System.gc();

        long freedMemory = Runtime.getRuntime().freeMemory() - memory;

        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
        sender.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCollected);
        sender.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCollected);
        sender.sendMessage(TextFormat.GOLD + "BlockType Entities: " + TextFormat.RED + tilesCollected);
        sender.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
        return true;
    }
}
