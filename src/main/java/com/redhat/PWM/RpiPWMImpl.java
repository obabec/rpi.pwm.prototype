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
            if (pin == 12) {
                pathToControll = "/sys/class/pwm/pwmchip0/pwm0";
                            } else {
                pathToControll = "/sys/class/pwm/pwmchip0/pwm1";
            }
        }
    }

    public void cleanUp() {

        try {
            Process p;
            if (pin == 12) {
                p = new ProcessBuilder("/bin/sh", "-c", "echo 0 > /sys/class/pwm/pwmchip0/unexport").start();
            } else {
                p = new ProcessBuilder("/bin/sh", "-c", "echo 1 > /sys/class/pwm/pwmchip0/unexport").start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


        try {
            File f = new File("/boot/config.txt");
            String config = "dtoverlay=pwm";
            if (checkFileConf(f,config)) {
                Writer configWriter;
                configWriter = new BufferedWriter(new FileWriter("/boot/config.txt",true));
                configWriter.append("dtoverlay=pwm");
                configWriter.close();
            }
            Process p;
            if (pin == 12) {
                p = new ProcessBuilder("/bin/sh", "-c", "echo 0 > /sys/class/pwm/pwmchip0/export").start();
            } else {
                p = new ProcessBuilder("/bin/sh", "-c", "echo 1 > /sys/class/pwm/pwmchip0/export").start();
            }
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

    public Boolean checkFileConf(File file, String conf) {

        int counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
               if (line.toLowerCase().equals(conf.toLowerCase())) {
                   counter++;
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (counter > 0) {
            return false;
        } else {
            return true;
        }
    }
}
