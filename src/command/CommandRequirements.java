package command;

public class CommandRequirements {

    private String name;
    private String[] args;
    private String help;

    public CommandRequirements(String name, String[] args, String help) {
        this.name = name;
        this.args = args;
        this.help = help;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public String getHelp() {
        return help;
    }
}
