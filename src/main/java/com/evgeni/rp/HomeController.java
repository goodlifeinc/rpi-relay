package com.evgeni.rp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PiController piController;

    @RequestMapping(path="/")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>(piController.getState(), HttpStatus.OK);
    }

    @RequestMapping(path = "on")
    public ResponseEntity<String> turnOn() {
        return new ResponseEntity<>(piController.turnOn(), HttpStatus.OK);
    }

    @RequestMapping(path = "off")
    public ResponseEntity<String> turnOff() {
        return new ResponseEntity<>(piController.turnOff(), HttpStatus.OK);
    }

    @RequestMapping(path = "temp")
    public ResponseEntity<String> getTemp() {
        logger.info("temperature endpoint hit");
        String temp = piController.getTemperature();
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
}
