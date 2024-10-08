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
        final String formattedNextFileName = newName.replaceAll("\"", "");
        File newFile = new File(wd, formattedNextFileName);

        try {
            currentFile.renameTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] concatFileName(String[] words){
        boolean hasStarted = false;
        String fileName = "";
        int startIndex = 0;
        int endIndex = 0;
        for(int i = 0; i<words.length; i++){
            if(!hasStarted & words[i].startsWith("\"")){
                hasStarted = true;
                startIndex = i;
                fileName += words[i] + " ";
            }
            else if(hasStarted && words[i].endsWith("\"")){
                fileName += words[i];
                endIndex = i;
                break;
            }
            else if(hasStarted){
                fileName += words[i] + " ";
            } 
        }

        if(endIndex == 0)
           return words; 

        String[] modified = new String[words.length - (endIndex - startIndex)];
        for(int i = 0; i < startIndex; i++){
            modified[i] = words[i];
        }

        modified[startIndex] = fileName.replaceAll("\"", "");

        for(int i = endIndex+1; i < words.length; i++){
            modified[i-endIndex+startIndex] = words[i]; 
        }
        return modified;
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
        final String formattedFileName = fileName.replaceAll("\"", "");
        File fileToLog = new File(wd, formattedFileName);

        try {
            System.out.println(readFile(fileToLog));
        } catch (FileNotFoundException e) {
            System.err.printf("%s does not exsist\n", fileToLog.getName());
        }
    }

    private void Wc(String fileName){
        final String formattedFileName = fileName.replaceAll("\"", "");
        File fileToScan = new File(wd, formattedFileName);

        try {
            final String lines = readFile(fileToScan);

            System.out.printf("%s/%s\nNumber of lines: %d\nNumber of words: %d\nNumber of characters: %d\n",
            wd.getName(),
            formattedFileName,
            lines.split("\n").length,
            lines.split(" ").length,
            lines.toCharArray().length);
        } catch (FileNotFoundException e) {
            System.err.printf("%s does not exsist\n", fileToScan.getName());
        }
    }

    private void MakeDir(String dirName){
        final String formattedDirName = dirName.replaceAll("\"", "");
        File nextDir = new File(wd, formattedDirName);
        nextDir.mkdir();
    }

    private void Touch(String fileName){
        final String formattedFileName = fileName.replaceAll("\"", "");
        File nextFile = new File(wd, fileName);

        try {
            nextFile.createNewFile();
        } catch (IOException e) {
            System.err.printf("Could not create file at \'%s/%s\'", wd.getName(), formattedFileName);
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
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\ncd <folder>");
                }
                else{
                    String[] cmd = concatFileName(command);
                    Cd(cmd[1]);
                }
                break;
            case "mv":
                if(command.length < 3){
                    System.err.println("Too few arguments!\nUsage:\nmv <existing-file> <new-file>");
                }else{
                    String[] cmd = concatFileName(command);
                    Mv(cmd[1], cmd[2]);
                }
                break;
            case "cat":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\ncat <file>");
                }else{
                    String[] cmd = concatFileName(command);
                    Cat(cmd[1]);
                }
                break;
            case "wc":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\nwc <file>");
                }else{
                    String[] cmd = concatFileName(command);
                    Wc(cmd[1]);
                }
                break;
            case "mkdir":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\nmkdir <directory-name>");
                }
                else{
                    String[] cmd = concatFileName(command);
                    MakeDir(cmd[1]);
                }
                break;
            case "touch":
                if(command.length < 2){
                    System.err.println("Too few arguments!\nUsage:\nmkdir <file>");
                }else{
                    String[] cmd = concatFileName(command);
                    Touch(cmd[1]);
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
