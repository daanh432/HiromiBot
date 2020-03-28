package nl.daanh.hiromibot.objects;

import java.util.List;


public interface CommandInterface {
    void handle(CommandContext ctx);

    String getHelp();

    String getInvoke();

    CATEGORY getCategory();

    default List<String> getAliases() {
        return List.of();
    }

    enum CATEGORY {
        FUN,
        MUSIC,
        MODERATION,
        OTHER
    }
}
