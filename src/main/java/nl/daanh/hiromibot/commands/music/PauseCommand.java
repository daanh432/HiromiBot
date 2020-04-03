package nl.daanh.hiromibot.commands.music;

import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class PauseCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        //TODO pause handler
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getInvoke() {
        return "pause";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ps", "pa");
    }
}
