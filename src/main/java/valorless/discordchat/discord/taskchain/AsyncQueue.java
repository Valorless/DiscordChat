package valorless.discordchat.discord.taskchain;

import java.util.concurrent.TimeUnit;

public interface AsyncQueue {
  void postAsync(Runnable paramRunnable);
  
  void shutdown(int paramInt, TimeUnit paramTimeUnit);
}


/* Location:              C:\Users\Minac\Desktop\HiberniaDiscord-3.0.1-SNAPSHOT\!\io\paradaux\hiberniadiscord\taskchain\AsyncQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */