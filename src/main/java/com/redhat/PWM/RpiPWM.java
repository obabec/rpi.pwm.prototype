package com.redhat.PWM;

public interface RpiPWM {
    void initialize();
    void cleanUp();
    void setDuty(long duty);
    void setFrequency(long frequency);
    void setPin(int pinNumber);
    void enable();
    void disable();

}
