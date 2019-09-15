package com.bsu.skc.order.api;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
//@HystrixCommand(fallbackMethod="")
public class Order {

    @GetMapping("/helloorder")
    public String helloStorage(){
        JSONObject jo = new JSONObject();
        jo.put("hello","order");
        return jo.toString();
    }

    @GetMapping("/order/exception")
    public String orderException(){
        JSONObject jo = new JSONObject();
        jo.put("order","exception");
        int exception = 1/0;                        //导致异常

        return jo.toString();
    }

    @GetMapping("/order/split-util")
    public String splitUtil(){
//        SplitUtil su = new SplitUtil();
        return "split-util";
    }
}
