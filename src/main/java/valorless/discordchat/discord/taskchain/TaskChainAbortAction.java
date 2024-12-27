/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ public interface TaskChainAbortAction<A1, A2, A3> {
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1) {}
/*    */   
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1, A2 arg2) {
/* 37 */     onAbort(chain, arg1);
/*    */   }
/*    */   
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1, A2 arg2, A3 arg3) {
/* 40 */     onAbort(chain, arg1, arg2);
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainAbortAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */