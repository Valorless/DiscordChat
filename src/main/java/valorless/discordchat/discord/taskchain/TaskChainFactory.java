/*     */ package valorless.discordchat.discord.taskchain;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.BiConsumer;
/*     */ 
/*     */ public class TaskChainFactory {
/*     */   private final GameInterface impl;
/*     */   
/*     */   private final AsyncQueue asyncQueue;
/*     */   
/*  36 */   private final Map<String, Queue<SharedTaskChain>> sharedChains = new HashMap<>();
/*     */   
/*     */   private volatile BiConsumer<Exception, TaskChainTasks.Task<?, ?>> defaultErrorHandler;
/*     */   
/*     */   volatile boolean shutdown = false;
/*     */   
/*     */   public TaskChainFactory(GameInterface impl) {
/*  42 */     this.impl = impl;
/*  43 */     this.asyncQueue = impl.getAsyncQueue();
/*  44 */     impl.registerShutdownHandler(this);
/*     */   }
/*     */   
/*     */   GameInterface getImplementation() {
/*  48 */     return this.impl;
/*     */   }
/*     */   
/*     */   public Map<String, Queue<SharedTaskChain>> getSharedChains() {
/*  52 */     return this.sharedChains;
/*     */   }
/*     */   
/*     */   public <T> TaskChain<T> newChain() {
/*  59 */     return new TaskChain<>(this);
/*     */   }
/*     */   
/*     */   public synchronized <T> TaskChain<T> newSharedChain(String name) {
/*  77 */     return new SharedTaskChain<>(name, this);
/*     */   }
/*     */   
/*     */   public BiConsumer<Exception, TaskChainTasks.Task<?, ?>> getDefaultErrorHandler() {
/*  86 */     return this.defaultErrorHandler;
/*     */   }
/*     */   
/*     */   public void setDefaultErrorHandler(BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/*  95 */     this.defaultErrorHandler = errorHandler;
/*     */   }
/*     */   
/*     */   public void shutdown(int duration, TimeUnit units) {
/* 104 */     this.shutdown = true;
/* 105 */     this.asyncQueue.shutdown(duration, units);
/*     */   }
/*     */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */