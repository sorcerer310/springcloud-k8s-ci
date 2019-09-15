package com.bsu.skc.storage2.api;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Storage {

    @GetMapping("/hellostorage")
    public String helloStorage2(){
        JSONObject jo = new JSONObject();
        jo.put("hello","storage2");
        return jo.toString();
    }
}
