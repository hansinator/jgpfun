package jgpfun.world2d;

/**
 *
 * @author hansinator
 */
public class WallSense {
    private final int worldWidth, worldHeight;

    private Body2d body;

    public int lastSenseVal = 0;

    public WallSense(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    void setBody(Body2d body) {
        this.body = body;
    }

    public int sense() {
        if(body.x < 0) {
            lastSenseVal = 0x1FF * 1;
            lastSenseVal = 0x1FF * 1;
        } else if(body.y < 0) {
            lastSenseVal = 0x1FF * 2;
        } else if(body.x > worldWidth) {
            lastSenseVal = 0x1FF * 3;
        } else if(body.y > worldHeight) {
            lastSenseVal = 0x1FF * 4;
        } else {
            lastSenseVal = 0;
        }

        return lastSenseVal;
    }
}
