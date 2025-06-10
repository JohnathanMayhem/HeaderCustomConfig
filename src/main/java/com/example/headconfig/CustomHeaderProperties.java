package com.example.headconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.http.header")
public class CustomHeaderProperties {

    /**
     * Enable or disable the FooBar header filter.
     * Default is true.
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}