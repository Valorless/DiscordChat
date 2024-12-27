/*    */ package valorless.discordchat.discord.taskchain;
/*    */ 
/*    */ public final class TaskChainDataWrappers {
/*    */   public static class Data2<D1, D2> {
/*    */     public final D1 var1;
/*    */     
/*    */     public final D2 var2;
/*    */     
/*    */     public Data2(D1 var1, D2 var2) {
/* 32 */       this.var1 = var1;
/* 33 */       this.var2 = var2;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Data3<D1, D2, D3> extends Data2<D1, D2> {
/*    */     public final D3 var3;
/*    */     
/*    */     public Data3(D1 var1, D2 var2, D3 var3) {
/* 39 */       super(var1, var2);
/* 40 */       this.var3 = var3;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Data4<D1, D2, D3, D4> extends Data3<D1, D2, D3> {
/*    */     public final D4 var4;
/*    */     
/*    */     public Data4(D1 var1, D2 var2, D3 var3, D4 var4) {
/* 46 */       super(var1, var2, var3);
/* 47 */       this.var4 = var4;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Data5<D1, D2, D3, D4, D5> extends Data4<D1, D2, D3, D4> {
/*    */     public final D5 var5;
/*    */     
/*    */     public Data5(D1 var1, D2 var2, D3 var3, D4 var4, D5 var5) {
/* 53 */       super(var1, var2, var3, var4);
/* 54 */       this.var5 = var5;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Data6<D1, D2, D3, D4, D5, D6> extends Data5<D1, D2, D3, D4, D5> {
/*    */     public final D6 var6;
/*    */     
/*    */     public Data6(D1 var1, D2 var2, D3 var3, D4 var4, D5 var5, D6 var6) {
/* 60 */       super(var1, var2, var3, var4, var5);
/* 61 */       this.var6 = var6;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChainDataWrappers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */