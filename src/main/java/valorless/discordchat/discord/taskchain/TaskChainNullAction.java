/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ @Deprecated
/*    */ public interface TaskChainNullAction<A1, A2, A3> extends TaskChainAbortAction<A1, A2, A3> {
/*    */   default void onNull(TaskChain<?> chain, A1 arg1) {}
/*    */   
/*    */   default void onNull(TaskChain<?> chain, A1 arg1, A2 arg2) {
/* 40 */     onNull(chain, arg1);
/*    */   }
/*    */   
/*    */   default void onNull(TaskChain<?> chain, A1 arg1, A2 arg2, A3 arg3) {
/* 43 */     onNull(chain, arg1, arg2);
/*    */   }
/*    */   
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1) {
/* 48 */     onNull(chain, arg1);
/*    */   }
/*    */   
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1, A2 arg2) {
/* 53 */     onNull(chain, arg1, arg2);
/*    */   }
/*    */   
/*    */   default void onAbort(TaskChain<?> chain, A1 arg1, A2 arg2, A3 arg3) {
/* 58 */     onNull(chain, arg1, arg2, arg3);
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainNullAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */