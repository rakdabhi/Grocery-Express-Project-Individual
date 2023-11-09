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

    public int getTime() {
        return this.time;
    }

    public void incrementTime(int delta) {
        this.time += delta;
    }

    /**
     * Returns the amount of sunlight that was emitted between a start time and end time.
     * Daylight is modelled as a sine/cosine function to simulate real day/night cycles,
     * with varying levels of sunlight throughout different times of the day.
     * The exact function to simulate the amount of sunlight emitted throughout the day is:
     * f(x) = -720 * cos((2π/1440)x) + 720
     * Thus, the amount of sunlight emitted within a given timeframe is the integral
     * over f(x) from startTime to endTime.
     * NOTE: Integral of above function is: ∫f(x)dx = -(518400/π) * sin((π/720)x) + 720x + C
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
     * Integral: ∫f(x)dx = -(518400/π) * sin((π/720)x) + 720x + C
     * NOTE: Constant 'C' gets cancelled out during integration.
     * @param x - input to ∫f(x)dx, point of time to calculate the amount of daylight
     * @return the amount of daylight at the given time
     */
    private static double daylightFunc(int x) {
        return -(518400/Math.PI) * Math.sin((Math.PI/720) * x) + (720 * x);
    }
}
