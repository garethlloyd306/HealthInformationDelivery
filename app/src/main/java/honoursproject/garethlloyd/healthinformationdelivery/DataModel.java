package honoursproject.garethlloyd.healthinformationdelivery;


public class DataModel {

    int fruit_and_veg;
    int water;
    String date;
    int steps;
    int activityTime;

    public int getFruitAndVeg(){
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
