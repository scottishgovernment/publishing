package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.onehippo.repository.events.PersistedHippoEventListenerRegistry;
import scot.mygov.publishing.eventlisteners.FunnelbackCuratorListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class FunnelbackInitialisingServletContextListener implements ServletContextListener {

    FunnelbackCuratorListener funnelbackCuratorListener;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Get Hystrix to publish metrics to JMX
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());

        // register cluster wide event listener to clear the page cache and the CRISP cache when curator change are made in funnelback
        funnelbackCuratorListener = new FunnelbackCuratorListener();
        PersistedHippoEventListenerRegistry.get().register(funnelbackCuratorListener);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PersistedHippoEventListenerRegistry.get().unregister(funnelbackCuratorListener);
    }
}
