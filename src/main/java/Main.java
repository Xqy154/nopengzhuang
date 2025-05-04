package me.yourname.nocolision;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Field;
public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        // 处理已存在的实体
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().forEach(this::disableCollision);
        });
        getLogger().info("§a碰撞禁用插件已激活");
    }
    // ➡️ 处理新生成的实体
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        disableCollision(e.getEntity());
    }
    // ➡️ 处理玩家加入
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        disableCollision(e.getPlayer());
    }
    // ⚙️ 核心碰撞禁用逻辑
    private void disableCollision(Entity entity) {
        if (entity instanceof LivingEntity) {
            try {
                // 获取NMS实体对象
                EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
                // 反射修改碰撞设置
                Field collisionField = Entity.class.getDeclaredField("af");
                collisionField.setAccessible(true);
                collisionField.set(nmsEntity, true); // af字段控制是否检测碰撞
                // 修正玩家碰撞体积
                if (entity instanceof Player) {
                    ((CraftPlayer) entity).getHandle().updateSize(0.0F, 0.0F);
                }
            } catch (ReflectiveOperationException ex) {
                getLogger().warning("反射操作失败: " + ex.getMessage());
            }
        }
    }
}