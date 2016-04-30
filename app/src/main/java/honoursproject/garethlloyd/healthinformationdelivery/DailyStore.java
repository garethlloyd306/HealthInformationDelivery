package honoursproject.garethlloyd.healthinformationdelivery;

/*
    Data store for the daily results
    Written by Gareth Lloyd
 */
public class DailyStore {
    private int fruit_and_veg;
    private int water;
    private String date;
    private int steps;
    private int activityTime;

    public int getFruitAndVeg() {
        return fruit_and_veg;
    }

    public void setFruitAndVeg(int fruit_and_veg) {
        this.fruit_and_veg = fruit_and_veg;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(int activityTime) {
        this.activityTime = activityTime;
    }

}
