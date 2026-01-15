package de.timing.announcer;

/**
 * Data model for an announcement.
 */
public class Announcement {

    private String name;
    private String message;
    private AnnouncementType type;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private int interval; // seconds between broadcasts (0 = manual only)
    private boolean enabled;

    public Announcement(String name, String message, AnnouncementType type) {
        this.name = name;
        this.message = message;
        this.type = type;
        this.subtitle = "";
        this.fadeIn = 10;
        this.stay = 70;
        this.fadeOut = 20;
        this.interval = 0;
        this.enabled = true;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AnnouncementType getType() {
        return type;
    }

    public void setType(AnnouncementType type) {
        this.type = type;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Create a deep copy of this announcement.
     */
    public Announcement copy() {
        Announcement copy = new Announcement(name, message, type);
        copy.setSubtitle(subtitle);
        copy.setFadeIn(fadeIn);
        copy.setStay(stay);
        copy.setFadeOut(fadeOut);
        copy.setInterval(interval);
        copy.setEnabled(enabled);
        return copy;
    }
}
