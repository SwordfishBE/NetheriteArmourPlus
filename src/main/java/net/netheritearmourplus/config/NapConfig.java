package net.netheritearmourplus.config;

/**
 * Server-side config for NetheriteArmourPlus.
 */
public final class NapConfig {

    private boolean enabled = true;
    private boolean useLuckPerms = false;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUseLuckPerms() {
        return useLuckPerms;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUseLuckPerms(boolean useLuckPerms) {
        this.useLuckPerms = useLuckPerms;
    }

    public void validate() {
        // Reserved for future config validation.
    }

    @Override
    public String toString() {
        return "NapConfig{enabled=" + enabled + ", useLuckPerms=" + useLuckPerms + "}";
    }
}
