package org.summer.warm.servlet;

import org.summer.warm.annotations.Autowired;
import org.summer.warm.annotations.Controller;
import org.summer.warm.annotations.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //resp.getWriter().write("404 Not Found!!!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        try {
            //1、加载配置文件
            doLoadConfxig(config.getInitParameter("contextConfigLocation"));
            //2、扫描相关的类
            doScanner(contextConfig.getProperty("scanPackage"));
            //3、初始化扫描到的类，并且将它们放入到 IOC 容器之中
            doInstance();
            //4、完成依赖注入
            doAutowired();
            //5、初始化HandlerMapping
            initHandlerMapping();
            System.out.println("GP Spring framework is init.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doAutowired() {

        ioc.forEach((simpleClazzName, obj) -> {
            Field[] declaredFields = obj.getClass().getDeclaredFields();

            Arrays.asList(declaredFields)
                    .stream()
                    .filter(field -> field.isAnnotationPresent(Autowired.class))
                    .forEach(
                            field -> {
                                Autowired fieldAnnotation = field.getAnnotation(Autowired.class);
                                String autoWireValue = fieldAnnotation.value();

                                Object instance = null;
                                if(!"".equals(autoWireValue)){
                                    instance = ioc.get(autoWireValue);
                                }

                                try {
                                    field.set(obj,instance);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
        });
    }

    private void doInstance() throws Exception {

        for (String clazzName : classNames) {
            Class<?> clazz = Class.forName(clazzName);

            if (clazz.isAnnotationPresent(Controller.class)) {

                Object instance = clazz.newInstance();
                ioc.put(initialToLow(clazz.getSimpleName()), instance);

            } else if (clazz.isAnnotationPresent(Service.class)) {
                Object instance = clazz.newInstance();
                ioc.put(initialToLow(clazz.getSimpleName()), instance);

                Service annotation = clazz.getAnnotation(Service.class);

                String serviceValue = annotation.value();
                if (!"".equals(serviceValue)) {
                    ioc.put(serviceValue, instance);
                }

                Class<?>[] interfaces = clazz.getInterfaces();

                for (Class<?> clazzz : interfaces) {
                    ioc.put(clazzz.getName(), instance);
                }
            }
        }
    }

    private String initialToLow(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File root = new File(url.getFile());
        for (File current : root.listFiles()) {
            if (!current.isDirectory()) {
                doScanner(scanPackage + "." + current);
            } else {
                if (!current.getName().endsWith(".class")) {
                    continue;
                }

                classNames.add(current.getName().replaceAll(".class", ""));
            }
        }
    }

    private void doLoadConfxig(String contextConfigLocation) throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        contextConfig.load(is);
    }
}
