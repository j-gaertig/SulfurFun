package de.jgaertig.sulfurFun.arena;

public class Goal {

    int corner_1_x, corner_1_y, corner_1_z, corner_2_x, corner_2_y, corner_2_z;

    public Goal(int corner_1_x, int corner_1_y, int corner_1_z, int corner_2_x, int corner_2_y, int corner_2_z) {
        this.corner_1_x = corner_1_x;
        this.corner_1_y = corner_1_y;
        this.corner_1_z = corner_1_z;
        this.corner_2_x = corner_2_x;
        this.corner_2_y = corner_2_y;
        this.corner_2_z = corner_2_z;
    }
}
