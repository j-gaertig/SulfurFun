package de.jgaertig.sulfurFun.arena;

public class Goal {

    double corner_1_x, corner_1_y, corner_1_z, corner_2_x, corner_2_y, corner_2_z;

    public Goal(double corner_1_x, double corner_1_y, double corner_1_z, double corner_2_x, double corner_2_y, double corner_2_z) {
        this.corner_1_x = corner_1_x;
        this.corner_1_y = corner_1_y;
        this.corner_1_z = corner_1_z;
        this.corner_2_x = corner_2_x;
        this.corner_2_y = corner_2_y;
        this.corner_2_z = corner_2_z;
    }
}
