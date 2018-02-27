package com.redhat;

import com.redhat.PWM.RpiPWMImpl;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        RpiPWMImpl pwmUsage = new RpiPWMImpl();

        pwmUsage.setPin(12);
        pwmUsage.initialize();
        pwmUsage.configPwm();
        pwmUsage.setFrequency(10000000);
        pwmUsage.setDuty(10000000);
        pwmUsage.enable();

        Thread.sleep(3000);

        for (int i = 10000000; i > 0; i -= 1000000) {
            System.out.println("Actual duty is: " + i);
            pwmUsage.setDuty(i);
            Thread.sleep(2000);
        }

        pwmUsage.disable();
        pwmUsage.cleanUp();
    }
}
