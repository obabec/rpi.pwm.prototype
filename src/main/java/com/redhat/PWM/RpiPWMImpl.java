package com.redhat.PWM;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class RpiPWMImpl implements RpiPWM {

    private Integer pin = null;
    private String pathToControll;
    private Logger logger = LoggerFactory.getLogger(RpiPWMImpl.class);
    private Writer fileWriter;
    public void initialize() {

        if (pin == null) {
            logger.warn("Pin is not selected!");
        } else {
            File sourceTemplate = new File("./resources/pwm-2chan-with-clk-overlay.dts");
            File destinationDirectory = new File("/boot/overlays");

            try {
                FileUtils.copyFileToDirectory(sourceTemplate,destinationDirectory);
            } catch (IOException e) {
                logger.warn("Error in copying overlay");
                e.printStackTrace();
            }

            if (pin == 18) {
                pathToControll = "/sys/class/pwm/pwmchip0/pwm0";
                            } else {
                pathToControll = "/sys/class/pwm/pwmchip0/pwm1";
            }

        }
    }

    public void cleanUp() {

    }

    public void setDuty(long duty) {

        writeAndWait(pathToControll + "/duty_cycle", String.valueOf(duty));
    }

    public void setFrequency(long frequency) {

        writeAndWait(pathToControll + "/period", String.valueOf(frequency));
    }

    public void setPin(int pinNumber) {
        this.pin = pinNumber;
    }

    public void enable() {

        writeAndWait(pathToControll + "/enable", "1");
    }

    public void disable() {

        writeAndWait(pathToControll + "/enable", "0");
    }

    public void configPwm(){

        Writer configWriter;
        try {
            configWriter = new BufferedWriter(new FileWriter("",true));
            configWriter.append("dtoverlay=pwm-2chan,pin=" + pin + ",func=4");
            configWriter.close();

            configWriter = new BufferedWriter(new FileWriter("/sys/class/pwm/pwmchip0/export"));
            if (pin == 18) {
             configWriter.write(0);
            } else {
                configWriter.write(1);
            }
            configWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAndWait(String path, String value) {

        try {
            fileWriter = new BufferedWriter(new FileWriter(path));
            fileWriter.write(value);
            Thread.sleep(2);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
