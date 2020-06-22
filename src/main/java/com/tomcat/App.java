package com.tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class App {
    public Tomcat tomcat;
    public Connector connector;
    public Engine engine;
    public Host host;
    public StandardContext context;
    public StandardRoot root;
    public DirResourceSet dirResourceSet;
    
    public void run() throws IOException, LifecycleException {
        String baseDir = Files.createTempDirectory("baseDir").toFile().getAbsolutePath();
        String docDir = Files.createTempDirectory("docDir").toFile().getAbsolutePath();
        
        tomcat = new Tomcat();
        
        connector = new Connector();
        connector.setPort(8081);
        
        tomcat.setConnector(connector);
        tomcat.setBaseDir(baseDir);
        
        engine = tomcat.getEngine();
        engine.setName("Tom-Cat");
        
        host = tomcat.getHost();
        host.setName("192.168.0.102");
        host.setAppBase("webapp");
        host.setAutoDeploy(true);
        
        String contextPath = "/show";
        context = (StandardContext) tomcat.addWebapp(contextPath, docDir);
        
        root = new StandardRoot(context);
        
        //加载Resources资源
        String WORK_HOME = System.getProperty("user.dir");
        File classesDir = new File(WORK_HOME,"target/classes");
        File jarDir = new File(WORK_HOME,"lib");
        dirResourceSet = new DirResourceSet();
        if(classesDir.exists()) {
            dirResourceSet.setRoot(root);
            dirResourceSet.setWebAppMount("/WEB-INF/classes");
            dirResourceSet.setBase(classesDir.getAbsolutePath());
            dirResourceSet.setInternalPath("/");
            
            root.addPreResources(dirResourceSet);
        }else if(jarDir.exists()) {
            dirResourceSet.setRoot(root);
            dirResourceSet.setWebAppMount("/WEB-INF/lib");
            dirResourceSet.setBase(jarDir.getAbsolutePath());
            dirResourceSet.setInternalPath("/");
            
            root.addJarResources(dirResourceSet);
        }else {
            root.addPostResources(new EmptyResourceSet((WebResourceRoot) dirResourceSet));
        }
        context.setResources(root);
        
        System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "*.jar");
        
        //启动
        tomcat.start();
        tomcat.getServer().await();
    }
    
    public static void main(String[] args) throws IOException, LifecycleException {
        new App().run();
    }
}
