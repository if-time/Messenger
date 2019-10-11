package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Path("/account")
public class AccountService {

    @GET
    @Path("/login")
    public String get() {
        return "You get the login.";
    }

    @POST
    @Path("/login")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post() {

        User user = new User();
        user.setName("Dong");
        user.setSex(22);
        return user;
    }
}
