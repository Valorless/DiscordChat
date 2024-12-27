/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public interface GameInterface {
/*    */   boolean isMainThread();
/*    */   
/*    */   AsyncQueue getAsyncQueue();
/*    */   
/*    */   void postToMain(Runnable paramRunnable);
/*    */   
/*    */   default void postAsync(Runnable run) {
/* 53 */     getAsyncQueue().postAsync(run);
/*    */   }
/*    */   
/*    */   void scheduleTask(int paramInt, Runnable paramRunnable);
/*    */   
/*    */   void registerShutdownHandler(TaskChainFactory paramTaskChainFactory);
/*    */   
/*    */   default void scheduleTask(int duration, TimeUnit units, Runnable run) {
/* 86 */     postAsync(() -> {
/*    */           try {
/*    */             Thread.sleep(units.toMillis(duration));
/*    */             run.run();
/* 90 */           } catch (InterruptedException e) {
/*    */             TaskChain.abort();
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\GameInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */