/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class TaskChainAsyncQueue implements AsyncQueue {
/* 33 */   private static final AtomicInteger threadId = new AtomicInteger();
/*    */   
/*    */   private final ThreadPoolExecutor executor;
/*    */   
/*    */   public TaskChainAsyncQueue() {
/* 37 */     this.executor = createCachedThreadPool();
/*    */   }
/*    */   
/*    */   public TaskChainAsyncQueue(ThreadPoolExecutor executor) {
/* 41 */     this.executor = executor;
/*    */   }
/*    */   
/*    */   public static ThreadPoolExecutor createCachedThreadPool() {
/* 45 */     return (ThreadPoolExecutor)Executors.newCachedThreadPool(r -> {
/*    */           Thread thread = new Thread(r);
/*    */           thread.setName("TaskChainAsyncQueue Thread " + threadId.getAndIncrement());
/*    */           return thread;
/*    */         });
/*    */   }
/*    */   
/*    */   public void postAsync(Runnable runnable) {
/* 53 */     this.executor.submit(runnable);
/*    */   }
/*    */   
/*    */   public void shutdown(int timeout, TimeUnit unit) {
/*    */     try {
/* 63 */       this.executor.setRejectedExecutionHandler((r, executor1) -> r.run());
/* 64 */       this.executor.shutdown();
/* 65 */       this.executor.awaitTermination(timeout, unit);
/* 66 */     } catch (InterruptedException e) {
/* 67 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainAsyncQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */