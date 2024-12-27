/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ final class TaskChainUtil {
/*    */   static void log(String log) {
/* 37 */     for (String s : log.split("\n"))
/* 38 */       Logger.getGlobal().info(s); 
/*    */   }
/*    */   
/*    */   public static void logError(String log) {
/* 43 */     for (String s : log.split("\n"))
/* 44 */       Logger.getGlobal().severe(s); 
/*    */   }
/*    */   
/*    */   static void sneakyThrows(Throwable t) {
/* 54 */     throw (RuntimeException)superSneaky(t);
/*    */   }
/*    */   
/*    */   private static <T extends Throwable> T superSneaky(Throwable t) throws T {
/* 65 */     throw (T)t;
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */