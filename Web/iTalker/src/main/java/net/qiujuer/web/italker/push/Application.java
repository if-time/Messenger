package net.qiujuer.web.italker.push;


import net.qiujuer.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

    public Application() {
        packages(AccountService.class.getPackage().getName());
    }
}
