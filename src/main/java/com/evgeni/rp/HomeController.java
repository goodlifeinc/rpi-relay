package com.evgeni.rp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @Autowired
    private PiController piController;

    @RequestMapping(path="/")
    public ResponseEntity<String> home() {
        return new ResponseEntity<String>(piController.getState(), HttpStatus.OK);
    }

    @RequestMapping(path = "/on")
    public ResponseEntity<String> turnOn() {
        return new ResponseEntity<String>(piController.turnOn(), HttpStatus.OK);
    }

    @RequestMapping(path = "off")
    public ResponseEntity<String> turnOff() {
        return new ResponseEntity<String>(piController.turnOff(), HttpStatus.OK);
    }
}
