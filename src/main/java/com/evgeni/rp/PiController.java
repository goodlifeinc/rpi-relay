package com.evgeni.rp;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PiController {
    private static final Logger logger = LoggerFactory.getLogger(PiController.class);

    final GpioController gpio = GpioFactory.getInstance();

    // provision gpio pin #01 as an output pin and turn on
    final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "MyLED", PinState.LOW);


//    final GpioPinDigitalOutput pinTemp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15, "MyTEMP", PinState.HIGH);

    PiController() {
        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);

        logger.info("PiController initialized");
    }

    public String turnOn() {
        if(pin.getState() == PinState.LOW) {
            String state = "Turned on";
            // toggle the current state of gpio pin #01 (should turn on)
            pin.toggle();
            logger.info(state);
            return state;
        }
        String state = "It is already on";
        logger.info(state);
        return state;
    }

    public String turnOff() {
        if(pin.getState() == PinState.HIGH) {
            // toggle the current state of gpio pin #01 (should turn on)
            pin.toggle();
            logger.info("Turned off");
            return "Turned off";
        }

        logger.info("It is already off");
        return "It is already off";
    }

    public String getState() {
        if (pin.getState() == PinState.HIGH) {
            logger.info("Check Status = On");
            return "On";
        }

        logger.info("Check Status = Off");
        return "Off";
    }

    public String getTemperature() {
        logger.info("Temperature Check START");
        W1Master master = new W1Master();
        List<W1Device> w1Devices = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
        List<String> temps = new ArrayList<>();
        logger.info("Entering for loop on W1Devies");
        for (W1Device device : w1Devices) {
            Double temp = ((TemperatureSensor) device).getTemperature();
            temps.add(temp.toString());
            logger.info("Temperature Check = " + temp.toString());
        }
        logger.info("Exiting for loop on W1Devies");

        return String.join(",", temps);
    }
}
