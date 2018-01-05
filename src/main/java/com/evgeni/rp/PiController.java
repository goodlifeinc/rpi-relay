package com.evgeni.rp;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.springframework.stereotype.Component;

@Component
public class PiController {
    final GpioController gpio = GpioFactory.getInstance();

    // provision gpio pin #01 as an output pin and turn on
    final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "MyLED", PinState.LOW);

    PiController() {
        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);
    }

    public String turnOn() {
        if(pin.getState() == PinState.LOW) {
            // toggle the current state of gpio pin #01 (should turn on)
            pin.toggle();
            return "Turned on";
        }

        return "It is already on";
    }

    public String turnOff() {
        if(pin.getState() == PinState.HIGH) {
            // toggle the current state of gpio pin #01 (should turn on)
            pin.toggle();
            return "Turned on";
        }

        return "It is already on";
    }

    public String getState() {
        if (pin.getState() == PinState.HIGH) {
            return "On";
        }

        return "Off";
    }
}
