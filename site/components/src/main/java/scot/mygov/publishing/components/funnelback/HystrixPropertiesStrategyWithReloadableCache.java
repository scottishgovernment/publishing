package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;

/**
 * Hystrix by default caches property values.  We want to manage the timeout in the repo and so to make any changes take
 * effect this class sets the command key to include the timeout value.
 */
class HystrixPropertiesStrategyWithReloadableCache extends HystrixPropertiesStrategy {

    @Override
    public String getCommandPropertiesCacheKey(HystrixCommandKey commandKey, HystrixCommandProperties.Setter setter) {
        return new StringBuilder(commandKey.name()).append(setter.getExecutionTimeoutInMilliseconds()).toString();
    }
}