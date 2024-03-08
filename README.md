# Spring Boot Starter Discord Example Project
This is an example project for [Spring Boot Starter Discord](https://github.com/ZGameLogic/Spring-Boot-Starter-Discord). 

## Spring information
Spring boot is a highly annotative framework to aid in creating event based applications. There are many popular spring starters that come with a lot of functionality and make things very easy in java. Some of the starters are [web](https://spring.io/guides/gs/spring-boot) (for rest APIs and website delivery), [JDBC](https://spring.io/projects/spring-data-jdbc) (for database connections) and [JPA](https://spring.io/projects/spring-data-jpa) (for JPA database data management). Most of these things come packaged with everything they need for you to easily integrate their functionality into your java application. 

## [JDA](https://github.com/discord-jda/JDA)
Java Discord API (JDA) is a java library that lets you create discord bots in java. It, too, is also event based. It listens for events from discord and allows the programmer to reply to those events. 

## Spring Boot Starter Discord
Spring boot starter discord, like the other starters, adds in discord bot management functionality into your java application. Like other spring libraries, it adds in custom annotations to help manage discord events as well as some application properties to configure the bot.

# Additions
### @DiscordController
Annotate any class with `@DiscordController` to tell spring this is a controller class for discord. If you do not annotate your class, your methods will not get called.
### @DiscordMapping
Annotate any method with `DiscordMapping` to tell spring to call this method when the event comes in from your bot.
| Field        |    description                                                              |
|--------------|:---------------------------------------------------------------------------:|
| Id           | Id of the event to listen to                                                |
| SubId        | Sub-id of the event to listen to. Useful for sub commands                   |
| FocusedOption| Focused option to listen to. Useful for CommandAutoCompleteInteractionEvent |
### @EventProperty
This annotation is used on method fields to automatically extract event properties from the event with the same name as the field.
If you need to use a different name than the field name, use the `name` parameter in the annotation.

# Example Project Explanation
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

## [DiscordGeneralListener.java](src/main/java/com/zgamelogic/controllers/DiscordGeneralListener.java)
This class has some small examples on how to do most common interactions. There are a few things I will draw attention to, everything else should be self-explanatory.
### Global Command Data
**Spring-Boot-Starter-Discord will automatically update global command data with any and all beans that contain CommandData**.
```java
@Bean
private List<CommandData> generalCommands(){
    /*
    This here is used to update the commands for your bot.
    You can set any number of beans with a list or single CommandData to be added to the global commands list on bot launch
     */
    return List.of(
            Commands.slash("ping", "Sends a ping to the bot"), // Slash command, so when the user types "/ping" in discord, this command and its description comes up
            Commands.user("Name user"), // User command, right-clicking a user and going to app > "Name user" will activate this command
            Commands.message("Count words"), // Message command, right-clicking a message and going to app > "Count words" will activate this command
            Commands.slash("options", "A command with some options").addOptions(
                    new OptionData(OptionType.STRING, "fruit", "Pick a fruit", true, true),
                    new OptionData(OptionType.USER, "user", "Pick a user", true)
            )
    );
}
```
This is an important block of code. In spring, Beans are initialized alongside of the controllers they are in.
The important thing here is that there is a bean (`@Bean`) in this discord controller class and that it returns some type of CommandData.
This could return any list of Command Data (such as list, linked list, array list) or any type of CommandData (such as SlashCommandData).
This command list has multiple different types of command data, so we use the parent CommandData class and throw it all in a list.
### Responding to Events
Spring boot starter discord has its own listener that it injects into the bot when spring launches. It listens for events and then calls methods in `@DiscordController` classes marked with `@DiscordMapping`.
```java
/**
 * This method gets called when the bot is logged in and ready.
 * This specifically updates global commands
 * @param event Ready Event from JDA
 */
@DiscordMapping
private void onReady(ReadyEvent event){
    log.info("Bot {} has finished loading", event.getJDA().getSelfUser().getName()); // Prints the name of the bot to the console
}
```
This is an example of "responding" to the ReadyEvent, which gets triggered when the bot is logged in and ready.
```java
/**
 * Simple slash command method mapping.
 * This will only proc on a slash command with an id of ping
 * @param event Slash Command Interaction Event from JDA
 */
@DiscordMapping(Id = "ping")
private void pingPongSlash(SlashCommandInteractionEvent event){
    event.reply("pong").queue();
}
```
This is an example of responding to a "ping" slash command. It will get triggered when a slash command with the id of `ping` is caught by the bot.
Take a closer look at the [file](src/main/java/com/zgamelogic/controllers/DiscordGeneralListener.java) for more other common events to listen to.

## [FormListener.java](src/main/java/com/zgamelogic/controllers/FormListener.java)
This is an example class of modals, another popular input method for discord.
### Form Slash Command
```java
@Bean
private SlashCommandData slashCommandData(){
    return Commands.slash("form", "Fill out this form please.");
}
```
Here we have our bean slash command. ID of `form`.
### Responding to the Form Slash Command
```java
@DiscordMapping(Id = "form")
private void formSlashCommand(SlashCommandInteractionEvent event){
    TextInput titleInput = TextInput.create(
            "title", // ID of input
            "Book Title",  // label of input
            TextInputStyle.SHORT) // Text input style
            .setPlaceholder("Too Like the Lightning") // Placeholder text, use this for an example
            .build(); // build the text input
    TextInput authorInput = TextInput.create("author", "Book Author", TextInputStyle.SHORT).setPlaceholder("Ada Palmer").build();
    TextInput shortDescription = TextInput.create("description", "Book Description", TextInputStyle.PARAGRAPH).setRequired(false).build();

    event.replyModal(
            Modal.create(
            "form_modal", // ID of the modal
                    "Create Book" // Title of the modal displayed to the user
            )
            .addActionRow(titleInput) // Add a title input row to your modal
            .addActionRow(authorInput) // Add an author input row to your modal
            .addActionRow(shortDescription) // Add a description input row to your modal
            .build() // Build your modal
    ).queue(); // Send the message back
}
```
This mapped method responds to the form slash command event. I will not go into more detail than code comments here on how to make modals. Go to the [JDA](https://github.com/discord-jda/JDA) to find out more info.
### Respond to the Modal Interaction Event
```java
@DiscordMapping(Id = "form_modal")
private void formModal(
        ModalInteractionEvent event, // Modal interaction event
        @EventProperty String title, // title field from the modal
        @EventProperty String author, // author field from the modal
        @EventProperty String description // description field from the modal
){
    event.reply("Thank you for your submission!").queue(); // Reply to the event with a simple message
    log.info("Boot title: {}", title); // print out the title to the log
    log.info("Boot author: {}", author); // print out the author to the log
    log.info("Boot description: {}", description); // print out the description to the log
}
```
This mapped method responds to the form_modal modal interaction event. The biggest thing of note here: `@EventProperty`.
This annotation is used on method fields to automatically extract event properties from the event. Since this modal has those three properties, SBSD will automatically call the method with the properties assigned.
SBSD will look for a property with the field name, or you can add a name property on the annotation to look for a specific name: `@EventProperty(name = "book_title") String custom`. This will assign any modal property called book_title to the string custom.