package org.xdty.phone.number.model.special;

public class Zone {
    public int max;
    public int min;
    public int desId;
    public String number;
    public boolean isWarning = false;

    Zone(int min, int max, int desId, boolean isWarning) {
        this.max = max;
        this.min = min;
        this.desId = desId;
        this.isWarning = isWarning;
    }

    public boolean inZone(String number) {
        try {
            long n = Long.parseLong(number);
            if (n >= min && n <= max) {
                this.number = number;
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Zone copy(String number) {
        Zone zone = new Zone(max, min, desId, isWarning);
        zone.number = number;
        return zone;
    }
}
