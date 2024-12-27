/*     */ package valorless.discordchat.discord.taskchain;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.plugin.Plugin;

/*     */ 
/*     */ public class BukkitTaskChainFactory extends TaskChainFactory {
/*     */   private BukkitTaskChainFactory(Plugin plugin, AsyncQueue asyncQueue) {
/*  40 */     super(new BukkitGameInterface(plugin, asyncQueue));
/*     */   }
/*     */   
/*     */   public static TaskChainFactory create(Plugin plugin) {
/*  44 */     return new BukkitTaskChainFactory(plugin, new TaskChainAsyncQueue());
/*     */   }
/*     */   
/*     */   private static class BukkitGameInterface implements GameInterface {
/*     */     private final Plugin plugin;
/*     */     
/*     */     private final AsyncQueue asyncQueue;
/*     */     
/*     */     BukkitGameInterface(Plugin plugin, AsyncQueue asyncQueue) {
/*  61 */       this.plugin = plugin;
/*  62 */       this.asyncQueue = asyncQueue;
/*     */     }
/*     */     
/*     */     public AsyncQueue getAsyncQueue() {
/*  67 */       return this.asyncQueue;
/*     */     }
/*     */     
/*     */     public boolean isMainThread() {
/*  72 */       return Bukkit.isPrimaryThread();
/*     */     }
/*     */     
/*     */     public void postToMain(Runnable run) {
/*  77 */       if (this.plugin.isEnabled()) {
/*  78 */         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, run);
/*     */       } else {
/*  80 */         run.run();
/*     */       } 
/*     */     }
/*     */     
/*     */     public void scheduleTask(int ticks, Runnable run) {
/*  86 */       if (this.plugin.isEnabled()) {
/*  87 */         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, run, ticks);
/*     */       } else {
/*  89 */         run.run();
/*     */       } 
/*     */     }
/*     */     
/*     */     public void registerShutdownHandler(final TaskChainFactory factory) {
/*  95 */       Bukkit.getPluginManager().registerEvents(new Listener() {
/*     */             @EventHandler
/*     */             public void onPluginDisable(PluginDisableEvent event) {
/*  98 */               if (event.getPlugin().equals(BukkitTaskChainFactory.BukkitGameInterface.this.plugin))
/*  99 */                 factory.shutdown(60, TimeUnit.SECONDS); 
/*     */             }
/*     */           },this.plugin);
/*     */     }
/*     */   }
/*     */   
/* 106 */   public static final TaskChainAbortAction<Player, String, ?> MESSAGE = new TaskChainAbortAction<Player, String, Object>() {
/*     */       public void onAbort(TaskChain<?> chain, Player player, String message) {
/* 109 */         player.sendMessage(message);
/*     */       }
/*     */     };
/*     */   
/* 112 */   public static final TaskChainAbortAction<Player, String, ?> COLOR_MESSAGE = new TaskChainAbortAction<Player, String, Object>() {
/*     */       public void onAbort(TaskChain<?> chain, Player player, String message) {
/* 115 */         player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
/*     */       }
/*     */     };
/*     */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\BukkitTaskChainFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */