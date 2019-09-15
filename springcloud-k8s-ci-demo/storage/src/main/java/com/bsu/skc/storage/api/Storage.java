package com.bsu.skc.storage.api;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Storage {

    @GetMapping("/hellostorage")
    public String helloStorage(){
        JSONObject jo = new JSONObject();
        jo.put("hello","storage");
        return jo.toString();
    }
}
