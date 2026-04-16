package net.netheritearmourplus.config;

/**
 * Server-side config for NetheriteArmourPlus.
 */
public final class NapConfig {

    private boolean enabled = true;
    private boolean useLuckPerms = false;
    private boolean armoredElytraSupport = true;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUseLuckPerms() {
        return useLuckPerms;
    }

    public boolean isArmoredElytraSupport() {
        return armoredElytraSupport;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUseLuckPerms(boolean useLuckPerms) {
        this.useLuckPerms = useLuckPerms;
    }

    public void setArmoredElytraSupport(boolean armoredElytraSupport) {
        this.armoredElytraSupport = armoredElytraSupport;
    }

    public void validate() {
        // Reserved for future config validation.
    }

    @Override
    public String toString() {
        return "NapConfig{enabled=" + enabled
                + ", useLuckPerms=" + useLuckPerms
                + ", armoredElytraSupport=" + armoredElytraSupport
                + "}";
    }
}
