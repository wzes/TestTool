package com.tongji.test;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class CashierSystem {


    public int calculate(int x, int y, int z) {
        int value =  x * 25 + y * 30 + z * 45;
        if(value <= 1000) {
            return value / 10;
        }
        else if (value > 1000 && value <= 1800) {
            return value * 3 / 20;
        }
        else if (value > 1800) {
            return value / 5;
        }
        return -1;
    }
}
