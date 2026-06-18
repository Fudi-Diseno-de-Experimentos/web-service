package synera.centralis.api.shared.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * Configuration for asynchronous processing in the application.
 * Enables async event processing for notifications and other background tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    /**
     * Sheds load with an observable warning instead of blocking the calling
     * (request/commit) thread the way CallerRunsPolicy would. These pools drive
     * fire-and-forget side-effects (domain events, notifications, FCM pushes);
     * under sustained overload, dropping a task with a logged warning is
     * preferable to stalling the request/transaction path.
     */
    private static final RejectedExecutionHandler LOGGING_DISCARD = (runnable, executor) ->
            log.warn("Async task rejected — pool saturated (active={}, poolSize={}, queued={}); task dropped",
                    executor.getActiveCount(), executor.getPoolSize(), executor.getQueue().size());


    /**
     * Creates a thread pool executor for async event processing
     * @return Configured thread pool executor
     */
    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Event-");
        executor.setRejectedExecutionHandler(LOGGING_DISCARD);
        executor.initialize();
        return executor;
    }
    
    /**
     * Creates a thread pool executor for notification processing
     * @return Configured thread pool executor for notifications
     */
    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Notification-");
        executor.setRejectedExecutionHandler(LOGGING_DISCARD);
        executor.initialize();
        return executor;
    }
    
    /**
     * Creates a thread pool executor for FCM notification processing
     * @return Configured thread pool executor for FCM notifications
     */
    @Bean(name = "fcmTaskExecutor")
    public Executor fcmTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("FCM-");
        executor.setRejectedExecutionHandler(LOGGING_DISCARD);
        executor.initialize();
        return executor;
    }
}