package com.seele.lilith.eureka.listener;

import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@SuppressWarnings("all")
public class EurekaInstanceCanceledListener implements ApplicationListener {

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        // Service down
        if (applicationEvent instanceof EurekaInstanceCanceledEvent) {
            EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent) applicationEvent;
            // Get node information in the current Eureka instance
            PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();
            Applications applications = registry.getApplications();
            // Traverse to obtain the node information of the registered nodes that is consistent with the current invalid node ID
            applications.getRegisteredApplications().forEach((registeredApplication) -> {
                registeredApplication.getInstances().forEach((instance) -> {
                    if (instance.getInstanceId().equals(event.getServerId())) {
                        log.error("Service：" + instance.getAppName() + " is down ...");
                        // TODO: message notification, just like wechat, sms, ....
                    }
                });
            });
        }

        if (applicationEvent instanceof EurekaInstanceRegisteredEvent) {
            EurekaInstanceRegisteredEvent event = (EurekaInstanceRegisteredEvent) applicationEvent;
            log.trace("Service ：" + event.getInstanceInfo().getAppName() + " registered successful");
        }

        if (applicationEvent instanceof EurekaInstanceRenewedEvent) {
            EurekaInstanceRenewedEvent event = (EurekaInstanceRenewedEvent) applicationEvent;
            log.trace("Heartbeat detection service：" + event.getInstanceInfo().getAppName() + "。。");
        }

        if (applicationEvent instanceof EurekaRegistryAvailableEvent){
            log.trace("Service Aualiable。。");
        }
    }
}
