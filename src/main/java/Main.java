import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        System.out.flush();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.print("$ ");
                System.out.flush();
                continue;
            }

            List<String> partsList = parseCommandLine(line);
            if(partsList.isEmpty()){
                System.out.print("$ ");
                System.out.flush();
                continue;
            }

            String command = partsList.get(0);
            String[] cmdArgs = partsList.subList(1, partsList.size()).toArray(new String[0]);

            switch (command) {
                case "exit":
                    return;

                case "echo":
                    handleEcho(cmdArgs);
                    break;

                case "type":
                    handleType(cmdArgs);
                    break;

                case "pwd":
                    handlePWD();
                    break;

                case "cd":
                    handleCD(cmdArgs);
                    break;

                default:
                    handleExternalCommands(command, cmdArgs);
            }

            System.out.print("$ ");
            System.out.flush();
        }

        scanner.close();
    }

    private static List<String> parseCommandLine(String line){

        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for(int i=0; i<line.length(); i++){
            char c = line.charAt(i);

            if( c == '\'' && !inDoubleQuote){
                // toggle single quote state (only if not inside double quotes)
                inSingleQuote = !inSingleQuote;
            }else if( c == '"' && !inSingleQuote){
                // toggle double quote state (only if not inside single quote)
                inDoubleQuote = !inDoubleQuote;

            }else if(Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote){
                // space outside quotes then it is a separate argument
                if(!current.isEmpty()){
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }else{
                // normal character
                current.append(c);
            }
        }

        if(!current.isEmpty()){
            tokens.add(current.toString());
        }
        return tokens;
    }
    private static void handleCD(String[] args) {



        if (args.length == 0) {
            return;
        }

        if(args[0].equals("~")){
            String home = System.getenv("HOME");
            if (home != null && !home.isEmpty()) {
                System.setProperty("user.dir", home);
            }
            return;
        }

        String path = args[0];
        File dir;

        if (path.startsWith("/")) {
            // absolute path
            dir = new File(path);
        } else {
            // relative path
            dir = new File(System.getProperty("user.dir"), path);
        }


        if (!dir.isDirectory()) {
            System.out.println("cd: " + path + ": No such file or directory");
            return;
        }


        try {
            // normalizing paths like ./ or ../
            String newPath = dir.getCanonicalPath();
            System.setProperty("user.dir", newPath);
        } catch (IOException e) {
            System.out.println("cd: " + path + ": No such file or directory");
        }
    }

    private static void handlePWD() {
        System.out.println(System.getProperty("user.dir"));
    }

    private static void handleExternalCommands(String command, String[] cmdArgs) {

        String executablePath = findExecutable(command);

        if(executablePath == null){
            System.out.println( command +": command not found");
            return;
        }

//        preparing command + arguments
        List<String> cmdList = new ArrayList<>();
        cmdList.add(command);
        cmdList.addAll( Arrays.asList(cmdArgs));

        // running the command using process builder
        ProcessBuilder pb = new ProcessBuilder(cmdList);
        pb.inheritIO(); // redirecting process output to shell output screen

        try{
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException e) {
            System.out.println("Error executing command: " + e.getMessage());
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String findExecutable(String command){

        // checking absolute and relative paths first
        File direct = new File(command);
        if( direct.exists() && direct.canExecute()){
            return direct.getAbsolutePath();
        }

        // otherwise searching all of the paths for command
        String pathEnv = System.getenv("PATH");

        if(pathEnv != null){
            for( String dir : pathEnv.split(":")){
                File file = new File(dir, command);
                if(file.exists() && file.canExecute()){
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private static void handleEcho(String[] args){
        System.out.println(String.join(" ",args));
    }

    private static void handleType(String[] args){
        if(args.length == 0){
            return;
        }

        Set<String> builtIns = new HashSet<>(Arrays.asList("echo","exit","type", "pwd"));
        String target = args[0];

        if(builtIns.contains(target)){
            System.out.println(target+" is a shell builtin");
            return;
        }

        String pathEnv = System.getenv("PATH");

        if(pathEnv != null){
            String[] paths = pathEnv.split(":");

            for(String dir : paths){
                File file = new File(dir, target);

                if(file.exists() && file.canExecute()){
                    System.out.println(target+ " is "+ file.getAbsolutePath());
                    return;
                }
            }
        }

            System.out.println(target+ ": not found");

    }
}
