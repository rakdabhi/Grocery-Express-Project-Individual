package edu.gatech.cs6310.classes;

public class Clock {
    private static Clock clock;
    private int time;

    /**
     * Private constructor to construct a Singleton Clock instance
     */
    private Clock() {
        this.time = 0;
    }

    /**
     * Static method to create and return a Singleton Clock instance
     * @return a Singleton Clock instance
     */
    public static synchronized Clock getInstance() {
        if (clock == null) {
            clock = new Clock();
        }
        return clock;
    }

    /**
     * Gets the current time
     * @return the current time
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Converts the current time into a Day-Hour-Minute format
     * @return the current time in Day-Hour-Minute format
     */
    public String getTimeDateFormat() {
        return this.getTimeDateFormat(this.time);
    }

    /**
     * Increments the clock based on a delta (change in time)
     * @param delta - amount of time to increment
     */
    public void incrementTime(int delta) {
        this.time += delta;
    }

    /**
     * Returns the amount of sunlight that was emitted between a start time and end time.
     * Daylight is modelled as a sine/cosine function to simulate real day/night cycles,
     * with varying levels of sunlight throughout different times of the day.
     * The exact function to simulate the amount of sunlight emitted throughout the day is:
     * f(x) = -5 * cos((2π/1440)x) + 5
     * Thus, the amount of sunlight emitted within a given timeframe is the integral
     * over f(x) from startTime to endTime.
     * NOTE: Integral of above function is: ∫f(x)dx = -(3600/π) * sin((π/720)x) + 5x + C
     * NOTE: The above equation has a period of 1440 to replicate 1440 minutes in a day
     *       so that startTime and endTime can be provided with minute units.
     * @param startTime - start time of timeframe in minutes
     * @param endTime - end time of timeframe in minutes
     * @return amount of daylight emitted from start time to end time
     */
    public static int getLightOverTime(int startTime, int endTime) {
        if (startTime >= endTime) {
            System.out.printf("ERROR:Start time %d is greater than or equal to end time %d!", startTime, endTime);
            return 0;
        }
        return (int) Math.round(daylightFunc(endTime) - daylightFunc(startTime));
    }

    /**
     * Helper method to help calculate the amount of sunlight at a given time.
     * This function is the integral of the daylight simulation function mentioned above.
     * Integral: ∫f(x)dx = -(3600/π) * sin((π/720)x) + 5x + C
     * NOTE: Constant 'C' gets cancelled out during integration.
     * @param x - input to ∫f(x)dx, point of time to calculate the amount of daylight
     * @return the amount of daylight at the given time
     */
    private static double daylightFunc(int x) {
        return -(3600/Math.PI) * Math.sin((Math.PI/720) * x) + (5 * x);
    }

    /**
     * Calculates the endTime based on a given startTime and a required minimum amount of light needed
     * @param minLightNeeded - minimum amount of light needed for a drone to cover a certain distance
     * @param startTime - time that drone will start charging in minutes
     * @return the end time of a timeframe in minutes given a start time and an amount of light needed
     */
    public static int getEndTime(int minLightNeeded, int startTime) {
        int endTime = startTime;
        double startTimeDayLight = daylightFunc(startTime); // start time daylight number is precalculated to save on re-computation in for-loop
        int light = 0;
        while (light < minLightNeeded) {
            endTime++;
            light = (int) Math.round(daylightFunc(endTime) - startTimeDayLight);
        }
        return endTime;
    }

    /**
     * Calculates the endTime based on the current time and a required minimum amount of light needed
     * @param minLightNeeded - minimum amount of light needed for a drone to cover a certain distance
     * @return the end time of a timeframe in minutes given an amount of light needed
     */
    public int getEndTime(int minLightNeeded) {
        return getEndTime(minLightNeeded, this.time);
    }

    /**
     * Converts a given date into a Day-Hour-Minute format
     * @param date - the date to convert
     * @return a String representing the date in Day-Hour-Minute format
     */
    public String getTimeDateFormat(int date) {
        if (date < 0) {
            System.out.printf("ERROR:date_%d_is_invalid", date);
            return "date_is_invalid";
        }
        int days = date / 1440;
        int hours = (date % 1440) / 60;
        int minutes = (date % 1440) % 60;
        String amOrPM = "AM";

        if (hours == 12) {
            amOrPM = "PM";
        } else if (hours >= 13) {
            hours -= 12;
            amOrPM = "PM";
        }
        return String.format("Day %d %02d:%02d%s", days, hours, minutes, amOrPM);
    }

    /**
     * Function returning this current time in String format
     * @return time written in String format
     */
    @Override
    public String toString() {
        return "[TIMESTAMP: " +
               this.time + " min (" +
               this.getTimeDateFormat() + ")]";
    }
}
