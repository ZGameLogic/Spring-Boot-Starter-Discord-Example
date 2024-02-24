# Spring Boot Starter Discord Example Project
This is an example project for [Spring Boot Starter Discord](https://github.com/ZGameLogic/Spring-Boot-Starter-Discord). 

## Spring information
Spring boot is a highly annotative framework to aid in creating event based applications. There are many popular spring starters that come with a lot of functionality and make things very easy in java. Some of the starters are [web](https://spring.io/guides/gs/spring-boot) (for rest APIs and website delivery), [JDBC](https://spring.io/projects/spring-data-jdbc) (for database connections) and [JPA](https://spring.io/projects/spring-data-jpa) (for JPA database data management). Most of these things come packaged with everything they need for you to easily integrate their functionality into your java application. 

## JDA
Java Discord API (JDA) is a java library that lets you create discord bots in java. It, too, is also event based. It listens for events from discord and allows the programmer to reply to those events. 

## Spring Boot Starter Discord
Spring boot starter discord, like the other starters, adds in discord bot management functionality into your java application. Like other spring libraries, it adds in custom annotations to help manage discord events as well as some application properties to configure the bot.

## [App.java](src/main/java/com/zgamelogic/application/App.java)
This is a classic spring start. 
```java
@SpringBootApplication(scanBasePackages = {
        "com.zgamelogic.controllers"
})
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }
}
```
The `@SpringBootAppliction` annotation is required on any spring application. Since we are using a controller not in the same package as this class, or in the default directory, we are required to explicitly define it in the `scanBasePackages` value of the annotation.
Like any java program, the entry point is in main. Here we can launch our spring app using the included shorthand spring gives us: `SpringApplication.run(App.class, args)`