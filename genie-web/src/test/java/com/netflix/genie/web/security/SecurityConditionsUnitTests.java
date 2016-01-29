/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.web.security;

import com.netflix.genie.test.categories.UnitTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;

import java.util.UUID;

/**
 * Tests for the Security Conditions.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class SecurityConditionsUnitTests {

    /**
     * Test the default constructor.
     */
    @Test
    public void testConstructor() {
        Assert.assertNotNull(new SecurityConditions());
    }

    /**
     * Test the AnySecurityEnabled class.
     */
    @Test
    public void testAnySecurityEnabledConfiguration() {
        final SecurityConditions.AnySecurityEnabled anySecurityEnabled = new SecurityConditions.AnySecurityEnabled();
        Assert.assertThat(
            anySecurityEnabled.getConfigurationPhase(),
            Matchers.is(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)
        );
    }

    /**
     * Test to make sure that when no supported security is enabled the class doesn't fire.
     *
     * @throws Exception on any error
     */
    @Test
    public void cantEnableBeanWithoutAnySecurityEnabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(SecurityEnabled.class);
        Assert.assertFalse(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure that when a supported security is enabled the class fires.
     *
     * @throws Exception on any error
     */
    @Test
    public void canEnableBeanWithSAMLEnabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            SecurityEnabled.class,
            "security.saml.enabled:true",
            "security.x509.enabled:false",
            "security.oauth2.enabled:false"
        );
        Assert.assertTrue(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure that when a supported security is enabled the class fires.
     *
     * @throws Exception on any error
     */
    @Test
    public void canEnableBeanWithX509Enabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            SecurityEnabled.class,
            "security.saml.enabled:false",
            "security.x509.enabled:true",
            "security.oauth2.enabled:false"
        );
        Assert.assertTrue(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure that when a supported security is enabled the class fires.
     *
     * @throws Exception on any error
     */
    @Test
    public void canEnableBeanWithOAuth2Enabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            SecurityEnabled.class,
            "security.saml.enabled:false",
            "security.x509.enabled:false",
            "security.oauth2.enabled:true"
        );
        Assert.assertTrue(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure that when a supported security is enabled the class fires.
     *
     * @throws Exception on any error
     */
    @Test
    public void canEnableBeanWithAllSecurityEnabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            SecurityEnabled.class,
            "security.saml.enabled:true",
            "security.x509.enabled:true",
            "security.oauth2.enabled:true"
        );
        Assert.assertTrue(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test the OnPingFederateEnabled class.
     */
    @Test
    public void testOnPingFederateEnabledConfiguration() {
        final SecurityConditions.OnPingFederateEnabled onPingFederateEnabled
            = new SecurityConditions.OnPingFederateEnabled();
        Assert.assertThat(
            onPingFederateEnabled.getConfigurationPhase(),
            Matchers.is(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)
        );
    }

    /**
     * Test to make sure when Ping federate configuration isn't right the bean doesn't exist.
     *
     * @throws Exception on any error
     */
    @Test
    public void cantEnableBeanWithPingFederateDisabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            PingFederateEnabled.class,
            "security.oauth2.enabled:false",
            "security.oauth2.pingfederate.enabled:false"
        );
        Assert.assertFalse(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure when Ping federate configuration isn't right the bean doesn't exist.
     *
     * @throws Exception on any error
     */
    @Test
    public void cantEnablePingFederateWithOauth2Disabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            PingFederateEnabled.class,
            "security.oauth2.enabled:false",
            "security.oauth2.pingfederate.enabled:true"
        );
        Assert.assertFalse(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure when Ping federate configuration isn't right the bean doesn't exist.
     *
     * @throws Exception on any error
     */
    @Test
    public void cantEnablePingFederateWithPingFederateDisabled() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            PingFederateEnabled.class,
            "security.oauth2.enabled:true",
            "security.oauth2.pingfederate.enabled:false"
        );
        Assert.assertFalse(context.containsBean("myBean"));
        context.close();
    }

    /**
     * Test to make sure when Ping federate configuration is right the bean exists.
     *
     * @throws Exception on any error
     */
    @Test
    public void canEnablePingFederate() throws Exception {
        final AnnotationConfigApplicationContext context = this.load(
            PingFederateEnabled.class,
            "security.oauth2.enabled:true",
            "security.oauth2.pingfederate.enabled:true"
        );
        Assert.assertTrue(context.containsBean("myBean"));
        context.close();
    }

    private AnnotationConfigApplicationContext load(final Class<?> config, final String... env) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context, env);
        context.register(config);
        context.refresh();
        return context;
    }

    /**
     * Configuration class for testing AnySecurityEnabled.
     */
    @Configuration
    @Conditional(SecurityConditions.AnySecurityEnabled.class)
    public static class SecurityEnabled {

        /**
         * Stupid placeholder for tests.
         *
         * @return The bean
         */
        @Bean
        public String myBean() {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Configuration class for testing OnPingFederate enabled class.
     */
    @Configuration
    @Conditional(SecurityConditions.OnPingFederateEnabled.class)
    public static class PingFederateEnabled {

        /**
         * Stupid placeholder for tests.
         *
         * @return The bean
         */
        @Bean
        public String myBean() {
            return UUID.randomUUID().toString();
        }
    }
}
