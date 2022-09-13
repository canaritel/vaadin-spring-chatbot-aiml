package org.vaadin.example;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.alicebot.ab.Bot;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 *
 */
@CssImport("./styles/shared-styles.css")
@Theme(variant = Lumo.DARK)
//@Push
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    private final String botPath;

    public Application(@Value("${bot.path}") String botPath) {
        this.botPath = botPath;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ScheduledExecutorService executorService() {
        return Executors.newScheduledThreadPool(10);
    }

    @EventListener
    public void createBots(ApplicationReadyEvent event) {
        File[] directories = new File(botPath + File.separator + "bots")
                .listFiles((FileFilter) FileFilterUtils.directoryFileFilter());

        try {

            if (directories != null) {
                Arrays.stream(directories)
                        .map(File::getName)
                        .map(name -> new Bot(name, botPath))
                        .forEach(bot -> event.getApplicationContext().getBeanFactory().registerSingleton(bot.name, bot));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // String resourcesPath = getResourcesPath();
//        Bot bot = new Bot(botName, botPath);
//        event.getApplicationContext().getBeanFactory().registerSingleton(botName, bot);
//        System.out.println("Ruta :" + botPath + " | Nombre del Bot: " + bot.name);
//
//        Bot bot2 = new Bot(botName2, botPath);
//        event.getApplicationContext().getBeanFactory().registerSingleton(botName2, bot);
//        System.out.println("Ruta :" + botPath + " | Nombre del Bot: " + bot2.name);

    }

    private static String getResourcesPath() {
        String resourcesPath;

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "bots";

        return resourcesPath;
    }

}
