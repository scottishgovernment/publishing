package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Get Hystrix to publish metrics to JMX
 */
public class HystrixServoMetricsPublisherInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
