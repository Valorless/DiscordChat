/*     */ package valorless.discordchat.discord.taskchain;
/*     */ 
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ public class TaskChainTasks {
/*     */   public static interface Task<R, A> {
/*     */     default TaskChain<?> getCurrentChain() {
/*  42 */       return TaskChain.getCurrentChain();
/*     */     }
/*     */     
/*     */     R run(A param1A);
/*     */   }
/*     */   
/*     */   public static interface FirstTask<R> extends Task<R, Object> {
/*     */     default R run(Object input) {
/*  55 */       return run();
/*     */     }
/*     */     
/*     */     R run();
/*     */   }
/*     */   
/*     */   public static interface LastTask<A> extends Task<Object, A> {
/*     */     default Object run(A input) {
/*  70 */       runLast(input);
/*  71 */       return null;
/*     */     }
/*     */     
/*     */     void runLast(A param1A);
/*     */   }
/*     */   
/*     */   public static interface GenericTask extends Task<Object, Object> {
/*     */     default Object run(Object input) {
/*  83 */       runGeneric();
/*  84 */       return null;
/*     */     }
/*     */     
/*     */     void runGeneric();
/*     */   }
/*     */   
/*     */   public static interface FutureTask<R, A> extends Task<R, A> {
/*     */     default R run(A input) {
/* 100 */       return null;
/*     */     }
/*     */     
/*     */     CompletableFuture<R> runFuture(A param1A);
/*     */   }
/*     */   
/*     */   public static interface FutureFirstTask<R> extends FutureTask<R, Object> {
/*     */     default CompletableFuture<R> runFuture(Object input) {
/* 113 */       return runFuture();
/*     */     }
/*     */     
/*     */     CompletableFuture<R> runFuture();
/*     */   }
/*     */   
/*     */   public static interface FutureGenericTask extends FutureTask<Object, Object> {
/*     */     default CompletableFuture<Object> runFuture(Object input) {
/* 127 */       return runFuture();
/*     */     }
/*     */     
/*     */     CompletableFuture<Object> runFuture();
/*     */   }
/*     */   
/*     */   public static interface AsyncExecutingTask<R, A> extends Task<R, A> {
/*     */     default R run(A input) {
/* 142 */       return null;
/*     */     }
/*     */     
/*     */     void runAsync(A param1A, Consumer<R> param1Consumer);
/*     */   }
/*     */   
/*     */   public static interface AsyncExecutingFirstTask<R> extends AsyncExecutingTask<R, Object> {
/*     */     default R run(Object input) {
/* 156 */       return null;
/*     */     }
/*     */     
/*     */     default void runAsync(Object input, Consumer<R> next) {
/* 161 */       run(next);
/*     */     }
/*     */     
/*     */     void run(Consumer<R> param1Consumer);
/*     */   }
/*     */   
/*     */   public static interface AsyncExecutingGenericTask extends AsyncExecutingTask<Object, Object> {
/*     */     default Object run(Object input) {
/* 174 */       return null;
/*     */     }
/*     */     
/*     */     default void runAsync(Object input, Consumer<Object> next) {
/* 179 */       run(() -> next.accept(null));
/*     */     }
/*     */     
/*     */     void run(Runnable param1Runnable);
/*     */   }
/*     */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainTasks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */