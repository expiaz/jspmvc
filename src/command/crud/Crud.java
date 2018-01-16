package command.crud;

import command.Command;
import command.CommandRequirements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crud implements Command {

    public void execute(String[] args) {
        if(args.length < 1) {
            System.out.println("Crud command uses like 'symfonee crud <className>'");
            System.exit(1);
        }

        String name = args[0];

        if(0 == name.length()) {
            System.out.println("<className> must be a conventionnal class name.");
            System.exit(1);
        }

        String normalized = Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);

        Crud gen = new Crud();
        try {
            gen.generate(normalized);
        } catch (IOException e) {
            System.out.println("An error occured while generating CRUD files.");
            System.exit(1);
        }
    }

    public CommandRequirements requirements() {
        return new CommandRequirements(
                "crud",
                new String[]{
                        "class"
                },
                "to generate CRUD files"
        );
    }

    private Map<String, String> infos;

    private static Pattern regex;
    static {
        regex = Pattern.compile("\\$\\$(\\w+)\\$\\$");
    }

    public Crud() {
    }

    private void generate(String name) throws IOException {

        this.infos = new HashMap<>();
        this.infos.put("className", name);
        this.infos.put("lowerCaseClassName", name.toLowerCase());

        Map<String, String> files = new HashMap<>();
        files.put("src/command/crud/stub/Controller.stub", "src/controller/" + this.infos.get("className") + "Controller.java");
        files.put("src/command/crud/stub/Repository.stub", "src/repository/" + this.infos.get("className") + "DAO.java");
        files.put("src/command/crud/stub/Entity.stub", "src/entity/" + this.infos.get("className") + ".java");

        files.put("src/command/crud/stub/view/add.stub", "web/WEB-INF/view/" + this.infos.get("lowerCaseClassName") + "/add.jsp");
        files.put("src/command/crud/stub/view/edit.stub", "web/WEB-INF/view/" + this.infos.get("lowerCaseClassName") + "/edit.jsp");
        files.put("src/command/crud/stub/view/list.stub", "web/WEB-INF/view/" + this.infos.get("lowerCaseClassName") + "/list.jsp");
        files.put("src/command/crud/stub/view/show.stub", "web/WEB-INF/view/" + this.infos.get("lowerCaseClassName") + "/show.jsp");

        Path stub;
        Path file;
        for(Map.Entry<String, String> entry : files.entrySet()) {
            File gen = new File(entry.getValue());

            file = Paths.get(gen.getAbsolutePath());
            stub = Paths.get(new File(entry.getKey()).getAbsolutePath());

            int fileSeparator = gen.getAbsolutePath().lastIndexOf("/");
            String directories = gen.getAbsolutePath().substring(0, fileSeparator);

            System.out.print("Crud : Generating " + entry.getValue() + " ... ");
            // creation des repertoires parents du nouveau fichier
            Files.createDirectories(Paths.get(directories));
            // creation et ecriture du fichier
            Files.write(file, this.replace(stub.toString()), Charset.forName("UTF-8"));
            System.out.println("Done.");
        }
        System.out.println("Crud : Finished generating files.");
    }

    protected List<String> replace(String filename) throws IOException {
        List<String> replacedLines = new ArrayList<>();
        String line;
        String replacedLine;
        Matcher matcher;
        int offset;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while((line = reader.readLine()) != null) {
            offset = 0;
            matcher = regex.matcher(line);
            replacedLine = line;
            while(matcher.find()) {
                replacedLine = replacedLine.substring(0, matcher.start() - offset)
                        + infos.get(matcher.group(1))
                        + replacedLine.substring(matcher.end() - offset);
                offset += matcher.group(0).length() - infos.get(matcher.group(1)).length();
            }
            replacedLines.add(replacedLine);
        }
        return replacedLines;
    }

}
