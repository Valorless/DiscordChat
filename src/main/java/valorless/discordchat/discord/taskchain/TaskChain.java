/*      */ package valorless.discordchat.discord.taskchain;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.function.BiConsumer;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.stream.Collectors;
/*      */ 
/*      */ public class TaskChain<T> {
/*   58 */   private static final ThreadLocal<TaskChain<?>> currentChain = new ThreadLocal<>();
/*      */   
/*      */   private final GameInterface impl;
/*      */   
/*      */   private final TaskChainFactory factory;
/*      */   
/*   62 */   private final Map<String, Object> taskMap = new HashMap<>(0);
/*      */   
/*   63 */   private final ConcurrentLinkedQueue<TaskHolder<?, ?>> chainQueue = new ConcurrentLinkedQueue<>();
/*      */   
/*   65 */   private int currentActionIndex = 0;
/*      */   
/*   66 */   private int actionIndex = 0;
/*      */   
/*      */   private boolean executed = false;
/*      */   
/*      */   private boolean async = false;
/*      */   
/*      */   private boolean done = false;
/*      */   
/*      */   private Object previous;
/*      */   
/*      */   private TaskHolder<?, ?> currentHolder;
/*      */   
/*      */   private Consumer<Boolean> doneCallback;
/*      */   
/*      */   private BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler;
/*      */   
/*      */   TaskChain(TaskChainFactory factory) {
/*   78 */     this.factory = factory;
/*   79 */     this.impl = factory.getImplementation();
/*      */   }
/*      */   
/*      */   public int getCurrentActionIndex() {
/*   91 */     return this.currentActionIndex;
/*      */   }
/*      */   
/*      */   public void setDoneCallback(Consumer<Boolean> doneCallback) {
/*  100 */     this.doneCallback = doneCallback;
/*      */   }
/*      */   
/*      */   public BiConsumer<Exception, TaskChainTasks.Task<?, ?>> getErrorHandler() {
/*  107 */     return this.errorHandler;
/*      */   }
/*      */   
/*      */   public void setErrorHandler(BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/*  116 */     this.errorHandler = errorHandler;
/*      */   }
/*      */   
/*      */   public static <D1, D2> TaskChainDataWrappers.Data2<D1, D2> multi(D1 var1, D2 var2) {
/*  126 */     return new TaskChainDataWrappers.Data2<>(var1, var2);
/*      */   }
/*      */   
/*      */   public static <D1, D2, D3> TaskChainDataWrappers.Data3<D1, D2, D3> multi(D1 var1, D2 var2, D3 var3) {
/*  133 */     return new TaskChainDataWrappers.Data3<>(var1, var2, var3);
/*      */   }
/*      */   
/*      */   public static <D1, D2, D3, D4> TaskChainDataWrappers.Data4<D1, D2, D3, D4> multi(D1 var1, D2 var2, D3 var3, D4 var4) {
/*  140 */     return new TaskChainDataWrappers.Data4<>(var1, var2, var3, var4);
/*      */   }
/*      */   
/*      */   public static <D1, D2, D3, D4, D5> TaskChainDataWrappers.Data5<D1, D2, D3, D4, D5> multi(D1 var1, D2 var2, D3 var3, D4 var4, D5 var5) {
/*  147 */     return new TaskChainDataWrappers.Data5<>(var1, var2, var3, var4, var5);
/*      */   }
/*      */   
/*      */   public static <D1, D2, D3, D4, D5, D6> TaskChainDataWrappers.Data6<D1, D2, D3, D4, D5, D6> multi(D1 var1, D2 var2, D3 var3, D4 var4, D5 var5, D6 var6) {
/*  154 */     return new TaskChainDataWrappers.Data6<>(var1, var2, var3, var4, var5, var6);
/*      */   }
/*      */   
/*      */   public static void abort() {
/*  165 */     TaskChainUtil.sneakyThrows(new AbortChainException());
/*      */   }
/*      */   
/*      */   public static TaskChain<?> getCurrentChain() {
/*  178 */     return currentChain.get();
/*      */   }
/*      */   
/*      */   public TaskChain<T> configure(Consumer<TaskChain<T>> configure) {
/*  195 */     configure.accept(this);
/*  196 */     return this;
/*      */   }
/*      */   
/*      */   public boolean hasTaskData(String key) {
/*  205 */     return this.taskMap.containsKey(key);
/*      */   }
/*      */   
/*      */   public <R> R getTaskData(String key) {
/*  217 */     return (R)this.taskMap.get(key);
/*      */   }
/*      */   
/*      */   public <R> R setTaskData(String key, Object val) {
/*  232 */     return (R)this.taskMap.put(key, val);
/*      */   }
/*      */   
/*      */   public <R> R removeTaskData(String key) {
/*  244 */     return (R)this.taskMap.remove(key);
/*      */   }
/*      */   
/*      */   public TaskChain<T> storeAsData(String key) {
/*  255 */     return current(val -> {
/*      */           setTaskData(key, val);
/*      */           return val;
/*      */         });
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> returnData(String key) {
/*  272 */     return currentFirst(() -> getTaskData(key));
/*      */   }
/*      */   
/*      */   public TaskChain<TaskChain<?>> returnChain() {
/*  280 */     return currentFirst(() -> this);
/*      */   }
/*      */   
/*      */   public TaskChain<T> delay(int gameUnits) {
/*  297 */     return currentCallback((input, next) -> this.impl.scheduleTask(gameUnits, () -> { }));
/*      */   }
/*      */   
/*      */   public TaskChain<T> delay(int duration, TimeUnit unit) {
/*  311 */     return currentCallback((input, next) -> this.impl.scheduleTask(duration, unit, () -> { }));
/*      */   }
/*      */   
/*      */   public TaskChain<?> abortChain() {
/*  327 */     if (this.executed) {
/*  328 */       abort();
/*  329 */       return this;
/*      */     } 
/*  331 */     return current(TaskChain::abort);
/*      */   }
/*      */   
/*      */   public TaskChain<T> abortIfNull() {
/*  342 */     return abortIfNull(null, null, null, null);
/*      */   }
/*      */   
public TaskChain<T> abortIfNull(TaskChainAbortAction<?, ?, ?> action, Object... args) {
    return abortIf(Predicate.isEqual(null), action, mapArgs(args));
}

public TaskChain<T> abortIf(T ifObj) {
    return abortIf(ifObj, null, mapArgs());
}

public TaskChain<T> abortIf(T ifObj, TaskChainAbortAction<?, ?, ?> action, Object... args) {
    return abortIf(Predicate.isEqual(ifObj), action, mapArgs(args));
}

public TaskChain<T> abortIf(Predicate<T> predicate) {
    return abortIf(predicate, null, mapArgs());
}

public TaskChain<T> abortIf(Predicate<T> predicate, TaskChainAbortAction<?, ?, ?> action, Object... args) {
    return current(obj -> {
        if (predicate.test(obj)) {
            // Map arguments and cast them for handleAbortAction
            Object[] mappedArgs = mapArgs(args);
            invokeAbortAction(action, mappedArgs);
            return null; // Mark chain as aborted
        }
        return obj; // Continue chain
    });
}

@SuppressWarnings("unchecked")
private void invokeAbortAction(TaskChainAbortAction<?, ?, ?> action, Object[] args) {
    if (action != null) {
        // Cast each argument to the appropriate type
        Object arg1 = args[0], arg2 = args[1], arg3 = args[2];
        handleAbortAction(
            (TaskChainAbortAction<Object, Object, Object>) action,
            arg1, arg2, arg3
        );
    }
}

@SuppressWarnings("unchecked")
private <A1, A2, A3> Object[] mapArgs(Object... args) {
    // Ensure an array of exactly 3 elements for handleAbortAction
    Object[] mapped = new Object[3];
    for (int i = 0; i < Math.min(args.length, 3); i++) {
        mapped[i] = args[i];
    }
    return mapped;
}


public TaskChain<T> abortIfNot(T ifNotObj, TaskChainAbortAction<?, ?, ?> action, Object... args) {
    return abortIf(Predicate.not(Predicate.isEqual(ifNotObj)), action, args);
}

public TaskChain<T> abortIfNot(Predicate<T> ifNotPredicate, TaskChainAbortAction<?, ?, ?> action, Object... args) {
    return abortIf(Predicate.not(ifNotPredicate), action, args);
}

/*      */   
/*      */   public <A1, A2, A3> TaskChain<T> abortIfNot(Predicate<T> ifNotPredicate, TaskChainAbortAction<A1, A2, A3> action, A1 arg1, A2 arg2, A3 arg3) {
/*  557 */     return abortIf(ifNotPredicate.negate(), action, arg1, arg2, arg3);
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> syncFirstCallback(TaskChainTasks.AsyncExecutingFirstTask<R> task) {
/*  584 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> asyncFirstCallback(TaskChainTasks.AsyncExecutingFirstTask<R> task) {
/*  595 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> currentFirstCallback(TaskChainTasks.AsyncExecutingFirstTask<R> task) {
/*  606 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> syncCallback(TaskChainTasks.AsyncExecutingTask<R, T> task) {
/*  627 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> syncCallback(TaskChainTasks.AsyncExecutingGenericTask task) {
/*  636 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> asyncCallback(TaskChainTasks.AsyncExecutingTask<R, T> task) {
/*  647 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> asyncCallback(TaskChainTasks.AsyncExecutingGenericTask task) {
/*  656 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> currentCallback(TaskChainTasks.AsyncExecutingTask<R, T> task) {
/*  667 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> currentCallback(TaskChainTasks.AsyncExecutingGenericTask task) {
/*  676 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> future(CompletableFuture<R> future) {
/*  694 */     return currentFuture(input -> future);
/*      */   }
/*      */   
/*      */   @SafeVarargs
/*      */   public final <R> TaskChain<List<R>> futures(CompletableFuture<R>... futures) {
/*  707 */     List<CompletableFuture<R>> futureList = new ArrayList<>(futures.length);
/*  708 */     Collections.addAll(futureList, futures);
/*  709 */     return futures(futureList);
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> futures(List<CompletableFuture<R>> futures) {
/*  721 */     return currentFuture(input -> getFuture(futures));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> syncFutures(TaskChainTasks.Task<List<CompletableFuture<R>>, T> task) {
/*  736 */     return syncFuture(input -> getFuture(task.run(input)));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> asyncFutures(TaskChainTasks.Task<List<CompletableFuture<R>>, T> task) {
/*  751 */     return asyncFuture(input -> getFuture(task.run(input)));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> currentFutures(TaskChainTasks.Task<List<CompletableFuture<R>>, T> task) {
/*  766 */     return currentFuture(input -> getFuture(task.run(input)));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> syncFirstFutures(TaskChainTasks.FirstTask<List<CompletableFuture<R>>> task) {
/*  781 */     return syncFuture(input -> getFuture(task.run()));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> asyncFirstFutures(TaskChainTasks.FirstTask<List<CompletableFuture<R>>> task) {
/*  796 */     return asyncFuture(input -> getFuture(task.run()));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<List<R>> currentFirstFutures(TaskChainTasks.FirstTask<List<CompletableFuture<R>>> task) {
/*  811 */     return currentFuture(input -> getFuture(task.run()));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> syncFirstFuture(TaskChainTasks.FutureFirstTask<R> task) {
/*  827 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> asyncFirstFuture(TaskChainTasks.FutureFirstTask<R> task) {
/*  838 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> currentFirstFuture(TaskChainTasks.FutureFirstTask<R> task) {
/*  849 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> syncFuture(TaskChainTasks.FutureTask<R, T> task) {
/*  865 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> syncFuture(TaskChainTasks.FutureGenericTask task) {
/*  874 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> asyncFuture(TaskChainTasks.FutureTask<R, T> task) {
/*  885 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> asyncFuture(TaskChainTasks.FutureGenericTask task) {
/*  894 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> currentFuture(TaskChainTasks.FutureTask<R, T> task) {
/*  905 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> currentFuture(TaskChainTasks.FutureGenericTask task) {
/*  914 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> syncFirst(TaskChainTasks.FirstTask<R> task) {
/*  931 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> asyncFirst(TaskChainTasks.FirstTask<R> task) {
/*  942 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> currentFirst(TaskChainTasks.FirstTask<R> task) {
/*  953 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> sync(TaskChainTasks.Task<R, T> task) {
/*  964 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> sync(TaskChainTasks.GenericTask task) {
/*  973 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> async(TaskChainTasks.Task<R, T> task) {
/*  984 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> async(TaskChainTasks.GenericTask task) {
/*  993 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public <R> TaskChain<R> current(TaskChainTasks.Task<R, T> task) {
/* 1004 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> current(TaskChainTasks.GenericTask task) {
/* 1013 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> syncLast(TaskChainTasks.LastTask<T> task) {
/* 1023 */     return add0(new TaskHolder<>(this, Boolean.valueOf(false), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> asyncLast(TaskChainTasks.LastTask<T> task) {
/* 1032 */     return add0(new TaskHolder<>(this, Boolean.valueOf(true), task));
/*      */   }
/*      */   
/*      */   public TaskChain<?> currentLast(TaskChainTasks.LastTask<T> task) {
/* 1041 */     return add0(new TaskHolder<>(this, null, task));
/*      */   }
/*      */   
/*      */   public void execute() {
/* 1049 */     execute((Consumer<Boolean>)null, (BiConsumer<Exception, TaskChainTasks.Task<?, ?>>)null);
/*      */   }
/*      */   
/*      */   public void execute(Runnable done) {
/* 1058 */     execute(finished -> done.run(), (BiConsumer<Exception, TaskChainTasks.Task<?, ?>>)null);
/*      */   }
/*      */   
/*      */   public void execute(Runnable done, BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/* 1068 */     execute(finished -> done.run(), errorHandler);
/*      */   }
/*      */   
/*      */   public void execute(Consumer<Boolean> done) {
/* 1077 */     execute(done, (BiConsumer<Exception, TaskChainTasks.Task<?, ?>>)null);
/*      */   }
/*      */   
/*      */   public void execute(BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/* 1085 */     execute((Consumer<Boolean>)null, errorHandler);
/*      */   }
/*      */   
/*      */   public void execute(Consumer<Boolean> done, BiConsumer<Exception, TaskChainTasks.Task<?, ?>> errorHandler) {
/* 1094 */     if (errorHandler == null)
/* 1095 */       errorHandler = this.factory.getDefaultErrorHandler(); 
/* 1097 */     this.doneCallback = done;
/* 1098 */     this.errorHandler = errorHandler;
/* 1099 */     execute0();
/*      */   }
/*      */   
/*      */   private <A1, A2, A3> void handleAbortAction(TaskChainAbortAction<A1, A2, A3> action, A1 arg1, A2 arg2, A3 arg3) {
/* 1106 */     if (action != null) {
/* 1107 */       TaskChain<?> prev = currentChain.get();
/*      */       try {
/* 1109 */         currentChain.set(this);
/* 1110 */         action.onAbort(this, arg1, arg2, arg3);
/* 1111 */       } catch (Exception e) {
/* 1112 */         TaskChainUtil.logError("TaskChain Exception in Abort Action handler: " + action.getClass().getName());
/* 1113 */         TaskChainUtil.logError("Current Action Index was: " + this.currentActionIndex);
/* 1114 */         e.printStackTrace();
/*      */       } finally {
/* 1116 */         currentChain.set(prev);
/*      */       } 
/*      */     } 
/* 1119 */     abort();
/*      */   }
/*      */   
/*      */   void execute0() {
/* 1123 */     synchronized (this) {
/* 1124 */       if (this.executed)
/* 1125 */         throw new RuntimeException("Already executed"); 
/* 1127 */       this.executed = true;
/*      */     } 
/* 1129 */     this.async = !this.impl.isMainThread();
/* 1130 */     nextTask();
/*      */   }
/*      */   
/*      */   void done(boolean finished) {
/* 1134 */     this.done = true;
/* 1135 */     if (this.doneCallback != null) {
/* 1136 */       TaskChain<?> prev = currentChain.get();
/*      */       try {
/* 1138 */         currentChain.set(this);
/* 1139 */         this.doneCallback.accept(Boolean.valueOf(finished));
/* 1140 */       } catch (Exception e) {
/* 1141 */         handleError(e, null);
/*      */       } finally {
/* 1143 */         currentChain.set(prev);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected TaskChain add0(TaskHolder<?, ?> task) {
/* 1150 */     synchronized (this) {
/* 1151 */       if (this.executed)
/* 1152 */         throw new RuntimeException("TaskChain is executing"); 
/*      */     } 
/* 1156 */     this.chainQueue.add(task);
/* 1157 */     return this;
/*      */   }
/*      */   
/*      */   private void nextTask() {
/* 1164 */     synchronized (this) {
/* 1165 */       this.currentHolder = this.chainQueue.poll();
/* 1166 */       if (this.currentHolder == null)
/* 1167 */         this.done = true; 
/*      */     } 
/* 1171 */     if (this.currentHolder == null) {
/* 1172 */       this.previous = null;
/* 1174 */       done(true);
/*      */       return;
/*      */     } 
/* 1178 */     Boolean isNextAsync = this.currentHolder.async;
/* 1179 */     if (isNextAsync == null || this.factory.shutdown) {
/* 1180 */       this.currentHolder.run();
/* 1181 */     } else if (isNextAsync.booleanValue()) {
/* 1182 */       if (this.async) {
/* 1183 */         this.currentHolder.run();
/*      */       } else {
/* 1185 */         this.impl.postAsync(() -> {
/*      */               this.async = true;
/*      */               this.currentHolder.run();
/*      */             });
/*      */       } 
/* 1191 */     } else if (this.async) {
/* 1192 */       this.impl.postToMain(() -> {
/*      */             this.async = false;
/*      */             this.currentHolder.run();
/*      */           });
/*      */     } else {
/* 1197 */       this.currentHolder.run();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void handleError(Throwable throwable, TaskChainTasks.Task<?, ?> task) {
/* 1203 */     Exception e = (throwable instanceof Exception) ? (Exception)throwable : new Exception(throwable);
/* 1204 */     if (this.errorHandler != null) {
/* 1205 */       TaskChain<?> prev = currentChain.get();
/*      */       try {
/* 1207 */         currentChain.set(this);
/* 1208 */         this.errorHandler.accept(e, task);
/* 1209 */       } catch (Exception e2) {
/* 1210 */         TaskChainUtil.logError("TaskChain Exception in the error handler!" + e2.getMessage());
/* 1211 */         TaskChainUtil.logError("Current Action Index was: " + this.currentActionIndex);
/* 1212 */         e.printStackTrace();
/*      */       } finally {
/* 1214 */         currentChain.set(prev);
/*      */       } 
/*      */     } else {
/* 1217 */       TaskChainUtil.logError("TaskChain Exception on " + ((task != null) ? task.getClass().getName() : "Done Hander") + ": " + e.getMessage());
/* 1218 */       TaskChainUtil.logError("Current Action Index was: " + this.currentActionIndex);
/* 1219 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void abortExecutingChain() {
/* 1224 */     this.previous = null;
/* 1225 */     this.chainQueue.clear();
/* 1226 */     done(false);
/*      */   }
/*      */   
/*      */   private <R> CompletableFuture<List<R>> getFuture(List<CompletableFuture<R>> futures) {
    CompletableFuture<List<R>> onDone = new CompletableFuture<>();
    CompletableFuture[] arrayOfCompletableFuture = futures.toArray(new CompletableFuture[0]);

    CompletableFuture.allOf(arrayOfCompletableFuture).whenComplete((aVoid, throwable) -> {
        if (throwable != null) {
            // If allOf itself fails
            onDone.completeExceptionally(throwable);
        } else {
            boolean[] error = { false };
            List<R> results = futures.stream()
                                     .map(future -> {
                                         try {
                                             return future.get(); // Retrieve the result
                                         } catch (Exception e) {
                                             error[0] = true; // Flag any failure
                                             return null;
                                         }
                                     })
                                     .collect(Collectors.toList());

            if (error[0]) {
                onDone.completeExceptionally(new Exception("Future Dependant had an exception"));
            } else {
                onDone.complete(results);
            }
        }
    });

    return onDone;
}
/*      */   
/*      */   private class TaskHolder<R, A> {
/*      */     private final TaskChain<?> chain;
/*      */     
/*      */     private final TaskChainTasks.Task<R, A> task;
/*      */     
/*      */     final Boolean async;
/*      */     
/*      */     private boolean executed = false;
/*      */     
/*      */     private boolean aborted = false;
/*      */     
/*      */     private final int actionIndex;
/*      */     
/*      */     private TaskHolder(TaskChain<?> chain, Boolean async, TaskChainTasks.Task<R, A> task) {
/* 1276 */       this.actionIndex = TaskChain.this.actionIndex++;
/* 1277 */       this.task = task;
/* 1278 */       this.chain = chain;
/* 1279 */       this.async = async;
/*      */     }
/*      */     
/*      */     private void run() {
/* 1286 */       Object arg = this.chain.previous;
/* 1287 */       this.chain.previous = null;
/* 1288 */       TaskChain.this.currentActionIndex = this.actionIndex;
/* 1290 */       TaskChain<?> prevChain = TaskChain.currentChain.get();
/*      */       try {
/* 1292 */         TaskChain.currentChain.set(this.chain);
/* 1293 */         if (this.task instanceof TaskChainTasks.FutureTask) {
/* 1295 */           CompletableFuture<R> future = ((TaskChainTasks.FutureTask)this.task).runFuture(arg);
/* 1296 */           if (future == null)
/* 1297 */             throw new NullPointerException("Must return a Future"); 
/* 1299 */           future.whenComplete((r, throwable) -> {
/*      */                 if (throwable != null) {
/*      */                   this.chain.handleError(throwable, this.task);
/*      */                   abort();
/*      */                 } else {
/*      */                   next(r);
/*      */                 } 
/*      */               });
/* 1307 */         } else if (this.task instanceof TaskChainTasks.AsyncExecutingTask) {
/* 1309 */           ((TaskChainTasks.AsyncExecutingTask)this.task).runAsync(arg, this::next);
/*      */         } else {
/* 1312 */           next(this.task.run((A)arg));
/*      */         } 
/* 1314 */       } catch (Throwable e) {
/* 1316 */         if (e instanceof AbortChainException) {
/* 1317 */           abort();
/*      */           return;
/*      */         } 
/* 1320 */         this.chain.handleError(e, this.task);
/* 1321 */         abort();
/*      */       } finally {
/* 1323 */         if (prevChain != null) {
/* 1324 */           TaskChain.currentChain.set(prevChain);
/*      */         } else {
/* 1326 */           TaskChain.currentChain.remove();
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     private synchronized void abort() {
/* 1335 */       this.aborted = true;
/* 1336 */       this.chain.abortExecutingChain();
/*      */     }
/*      */     
/*      */     private void next(Object resp) {
/* 1343 */       synchronized (this) {
/* 1344 */         if (this.aborted) {
/* 1345 */           this.chain.done(false);
/*      */           return;
/*      */         } 
/* 1348 */         if (this.executed) {
/* 1349 */           this.chain.done(false);
/* 1350 */           throw new RuntimeException("This task has already been executed.");
/*      */         } 
/* 1352 */         this.executed = true;
/*      */       } 
/* 1355 */       this.chain.async = !TaskChain.this.impl.isMainThread();
/* 1356 */       this.chain.previous = resp;
/* 1357 */       this.chain.nextTask();
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\TaskChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */