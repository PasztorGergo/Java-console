import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Menu {
    File wd;

    public Menu(){
        wd = new File(System.getProperty("user.dir"));
        while(true){
            String[] commandAndParams = ProcessCommand();
            MenuCases(commandAndParams);
        }
    }

    private void Ls(String ...flag){
        for(File f : wd.listFiles()){
            if(flag[0].equals("-l"))
                System.out.printf("%c %s %sB\n", f.isFile() ? 'f' : 'd', f.getName(), f.length());
            else
                System.out.printf("%s ",f.getName());
        }
        System.out.println();
    }

    private boolean checkFileExistance(File nextDir){
        if(!nextDir.exists()){
            System.err.printf("%s does not exsist\n", nextDir.getName());
            return false;
        }
        return true;
    }

    private void Cd(String location){
        final String loc = location.replaceAll("\"", "");
        if(loc.equals("..")){
            wd = wd.getParentFile();
            return;
        }

        File nextDir = new File(wd, loc);
        if(!checkFileExistance(nextDir)){
            return;
        }

        wd = nextDir;
    }

    private String Pwd(){
        try{
            final String pwd = wd.getCanonicalPath();
            return String.format("%s\ntotal %d", pwd, wd.listFiles().length);
        } catch (IOException e){
            return e.getMessage();
        }
    }

    private void Mv(String currentName, String newName){
        File currentFile = new File(wd, currentName.replaceAll("\"", ""));
        if(!checkFileExistance(currentFile))
            return;

        File newFile = new File(wd, newName);

        try {
            currentFile.renameTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] ProcessCommand(){
        Scanner scanner = new Scanner(System.in);

        String[] commandAndParameters = scanner.nextLine().split(" ");
        return commandAndParameters;
    }

    private String readFile(File file) throws FileNotFoundException{
        Scanner fileScanner = new Scanner(file);
        String lines = "";
        while(fileScanner.hasNextLine()){
            lines += fileScanner.nextLine();
            lines += "\n";
        }
        fileScanner.close();
        return lines;
    }

    private void Cat(String fileName){
        File fileToLog = new File(wd, fileName);

        try {
            System.out.println(readFile(fileToLog));
        } catch (FileNotFoundException e) {
            System.err.printf("%s does not exsist\n", fileToLog.getName());
        }
    }

    private void Wc(String fileName){
        File fileToScan = new File(wd, fileName);

        try {
            final String lines = readFile(fileToScan);

            System.out.printf("%s/%s\nNumber of lines: %d\nNumber of words: %d\nNumber of characters: %d\n",
            wd.getName(),
            fileName,
            lines.split("\n").length,
            lines.split(" ").length,
            lines.toCharArray().length);
        } catch (FileNotFoundException e) {
            System.err.printf("%s does not exsist\n", fileToScan.getName());
        }
    }

    private void MenuCases(String[] command){
        switch (command[0]) {
            case "exit":
                System.exit(0);
                break;
            case "pwd":
                System.out.println(Pwd());
                break;
            case "ls":
                Ls(command.length > 1 ? command[1] : "");
                break;
            case "cd":
                Cd(command[1]);
                break;
            case "mv":
                if(command.length < 3){
                    System.err.println("Too few arguments!\nUsage:\nmv <existing-file> <new-file>");
                }else{
                    Mv(command[1], command[2]);
                }
                break;
            case "cat":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\ncat <file>");
                }else{
                    Cat(command[1]);
                }
                break;
            case "wc":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\nwc <file>");
                }else{
                    Wc(command[1]);
                }
                break;
            case "":
                break;
            default:
                System.err.printf("bash: %s: command not found\n", command);
                break;
        }
    }
}
