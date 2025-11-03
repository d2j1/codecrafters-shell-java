import java.util.Arrays;
import java.util.Scanner;

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

            if(command.equalsIgnoreCase("exit")){
                break;
            }else if( command.equals("echo")){
                handleEcho(cmdArgs);
            }else {
            System.out.println(command + ": command not found");
            }


        }

        scanner.close();

    }

    private static void handleEcho(String[] args){
        System.out.println(String.join(" ",args));
    }
}
