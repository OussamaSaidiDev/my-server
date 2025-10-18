package org.example.controllers;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.example.core.Controller;
import java.io.StringWriter;

@Controller(path = "/api/test")
public class TestController {

    public String get() {
        VelocityContext context = new VelocityContext();
        StringWriter stringWriter = new StringWriter();
        Velocity.getTemplate("templates/test-get.vm").merge(context, stringWriter);
        return stringWriter.toString();
    }

    public String post(String post) {
        VelocityContext context = new VelocityContext();
        context.put("post", post);
        StringWriter stringWriter = new StringWriter();
        Velocity.getTemplate("templates/test-post.vm").merge(context, stringWriter);
        return stringWriter.toString();
    }
}