package command;

import command.crud.Crud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandLine {

    private static Command[] commands = new Command[]{
        new Crud()
    };

    public static void main(String[] args) {

        Map<String, Command> instances = new HashMap<>();
        Map<String, String[]> availables = new HashMap<>();
        Map<String, String> commandHelps = new HashMap<>();

        StringBuilder help = new StringBuilder();

        CommandRequirements rq;
        for(Command cmd : commands) {
            rq = cmd.requirements();
            instances.put(rq.getName(), cmd);
            availables.put(rq.getName(), rq.getArgs());

            help.append("\t- '");
            help.append(rq.getName());
            help.append(" ");
            commandHelps.put(rq.getName(), "<" + String.join("> <", rq.getArgs()) + "> : " + rq.getHelp());
            help.append(commandHelps.get(rq.getName()));
            help.append("' ");
            help.append(rq.getHelp());
            help.append("\n");
        }

        if(0 == args.length || args[0].equals("-h")) {
            System.out.println("This is the command line helper for the Symfonee project.");
            System.out.println("Available commands are :");
            System.out.println(help.toString());
            System.exit(0);
        }

        String commandName = args[0];

        if(! availables.containsKey(commandName)) {
            System.out.println(commandName + " command not found.");
            System.exit(0);
        }

        if(args.length - 1 < availables.get(commandName).length) {
            System.out.println(commandHelps.get(commandName));
            System.exit(0);
        }

        instances.get(commandName).execute(Arrays.copyOfRange(args, 1, args.length));
    }

}
