package com.pay.demo.Controller;

import com.pay.demo.service.DemoService;
import org.summer.warm.annotations.Autowired;
import org.summer.warm.annotations.Controller;
import org.summer.warm.annotations.RequestMapping;
import org.summer.warm.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("query")
    public void query(HttpServletRequest req, HttpServletResponse resp,@RequestParam("id") String id){
        String result = demoService.query(id);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("create")
    public void create(HttpServletRequest req, HttpServletResponse resp,@RequestParam String message){
        demoService.insert(message);
    }
}
