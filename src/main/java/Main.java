import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage

        Scanner scanner = new Scanner(System.in);

        while(true){

            System.out.print("$ ");

            //block untill user press enter
            if(!scanner.hasNextLine()){
                break;
            }

            String line = scanner.nextLine().trim();

            if(line.isEmpty()){
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0];
            String[] cmdArgs = Arrays.copyOfRange(parts, 1, parts.length);

            switch (command){
                case "exit":
                    return;

                case "echo":
                    handleEcho(cmdArgs);
                    break;

                case "type":
                    handleType(cmdArgs);
                    break;

                default:
//                    System.out.println(command + ": command not found");
                    handleExternalCommands(command, cmdArgs);

            }
        }

        scanner.close();

    }

    private static void handleExternalCommands(String command, String[] cmdArgs) {

        String executablePath = findExecutable(command);

        if(executablePath == null){
            System.out.println("Program was passed "+ cmdArgs.length+" args (including program name).");
            return;
        }

//        preparing command + arguments
        List<String> cmdList = new ArrayList<>();
        cmdList.add(executablePath);
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

        Set<String> builtIns = new HashSet<>(Arrays.asList("echo","exit","type"));
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
