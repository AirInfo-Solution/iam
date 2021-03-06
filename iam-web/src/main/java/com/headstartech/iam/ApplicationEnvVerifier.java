package com.headstartech.iam;

import com.headstartech.iam.core.annotations.Dev;
import com.headstartech.iam.core.annotations.Prod;
import com.headstartech.iam.core.annotations.QA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public class ApplicationEnvVerifier implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEnvVerifier.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEvent) {
        Environment env = applicationEvent.getEnvironment();
        Set<String> res = Arrays.stream(env.getActiveProfiles()).filter(s -> Dev.name.equals(s) || QA.name.equals(s) || Prod.name.equals(s)).collect(Collectors.toSet());
        if(res.isEmpty() ||res.size() > 1) {
            logger.error("Exactly one of {}, {}, {} profiles must be set as active! (spring.profiles.active)", Dev.name, QA.name, Prod.name);
            System.exit(1);
        }
    }

}

