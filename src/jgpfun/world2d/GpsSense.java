package jgpfun.world2d;

/**
 *
 * @author hansinator
 */
public class GpsSense {

    private final Body2d body;


    public GpsSense(Body2d body) {
        this.body = body;
    }

    public int getX() {
        return body.x;
    }

    public int getY() {
        return body.y;
    }
}
