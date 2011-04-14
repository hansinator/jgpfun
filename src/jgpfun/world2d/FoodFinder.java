package jgpfun.world2d;

import java.util.List;

public class FoodFinder {

    List<Food> food;


    public FoodFinder(List<Food> food) {
        this.food = food;
    }


    public double foodDist(Food f, int x, int y) {
        return Math.sqrt(((x - f.x) * (x - f.x)) + ((y - f.y) * (y - f.y)));
    }


    public Food findNearestFood(int x, int y) {
        double minDist = 1000000;
        double curDist;
        int indexMinDist = -1;
        for (int i = 0; i < food.size(); i++) {
            curDist = foodDist(food.get(i), x, y);
            //limit visible range to 200
            //if (curDist > 200)
                //continue;
            if (curDist < minDist) {
                minDist = curDist;
                indexMinDist = i;
            }
        }
        if (indexMinDist > -1) {
            return food.get(indexMinDist);
        } else {
            return new Food(x, y);
        }
    }
}
