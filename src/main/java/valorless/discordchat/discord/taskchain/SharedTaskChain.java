/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Queue;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Consumer;
/*    */ 
/*    */ class SharedTaskChain<R> extends TaskChain<R> {
/*    */   private final String name;
/*    */   
/*    */   private final Map<String, Queue<SharedTaskChain>> sharedChains;
/*    */   
/*    */   private Queue<SharedTaskChain> queue;
/*    */   
/*    */   private volatile boolean isPending;
/*    */   
/*    */   private volatile boolean canExecute = true;
/*    */   
/*    */   SharedTaskChain(String name, TaskChainFactory factory) {
/* 43 */     super(factory);
/* 44 */     this.sharedChains = factory.getSharedChains();
/* 45 */     this.name = name;
/* 47 */     synchronized (this.sharedChains) {
/* 48 */       this.queue = this.sharedChains.get(this.name);
/* 49 */       if (this.queue == null) {
/* 50 */         this.queue = new ConcurrentLinkedQueue<>();
/* 51 */         this.sharedChains.put(this.name, this.queue);
/*    */       } 
/* 53 */       this.queue.add(this);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void execute(Consumer<Boolean> done, BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/*    */     boolean shouldExecute;
/* 59 */     setErrorHandler(errorHandler);
/* 60 */     setDoneCallback(finished -> {
/*    */           setDoneCallback(done);
/*    */           done(finished.booleanValue());
/*    */           processQueue();
/*    */         });
/* 67 */     synchronized (this.sharedChains) {
/* 68 */       this.isPending = (this.queue.peek() != this);
/* 69 */       shouldExecute = (!this.isPending && this.canExecute);
/* 70 */       if (shouldExecute)
/* 71 */         this.canExecute = false; 
/*    */     } 
/* 74 */     if (shouldExecute)
/* 75 */       execute0(); 
/*    */   }
/*    */   
/*    */   private void processQueue() {
/*    */     SharedTaskChain next;
/* 83 */     this.queue.poll();
/* 85 */     synchronized (this.sharedChains) {
/* 86 */       next = this.queue.peek();
/* 87 */       if (next == null) {
/* 88 */         this.sharedChains.remove(this.name);
/*    */         return;
/*    */       } 
/* 91 */       if (!next.isPending)
/*    */         return; 
/* 95 */       this.canExecute = false;
/*    */     } 
/* 98 */     next.execute0();
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\SharedTaskChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */