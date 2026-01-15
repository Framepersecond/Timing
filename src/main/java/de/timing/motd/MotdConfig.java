package de.timing.motd;

/**
 * Data model for the server MOTD with customization.
 */
public class MotdConfig {

    private String line1;
    private String line2;
    private boolean enabled;

    public MotdConfig() {
        this.line1 = "<gradient:gold:yellow><bold>My Server</bold></gradient>";
        this.line2 = "<gray>Welcome to the server!</gray>";
        this.enabled = true;
    }

    public MotdConfig(String line1, String line2, boolean enabled) {
        this.line1 = line1;
        this.line2 = line2;
        this.enabled = enabled;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the full MOTD as a single string with newline.
     */
    public String getFullMotd() {
        return line1 + "\n" + line2;
    }

    /**
     * Create a copy of this config.
     */
    public MotdConfig copy() {
        return new MotdConfig(line1, line2, enabled);
    }
}
