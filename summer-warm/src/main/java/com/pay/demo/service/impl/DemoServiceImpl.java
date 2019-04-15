package com.pay.demo.service.impl;

import com.pay.demo.service.DemoService;
import org.summer.warm.annotations.Service;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String query(String id) {
        return "TOM";
    }

    @Override
    public void insert(String message) {
        System.out.println("insert==> "+message);
    }
}
