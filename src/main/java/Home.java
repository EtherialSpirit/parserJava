import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Home {

    public static void main(String[] args) {

        try {

            Home main = new Home();
            File file = main.getFileFromResources("ObjectModule.bsl");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine(); //Текущая строка
            int num = 1; //Номер строки

            String tempLine = ""; //переменная строки для неполных строк
            String tempNum = ""; //переменная номера строки для неполных строк
            String patternR;    //Регулярное выражение
            String delimeter;  //Разделитель

            File newTxtFile = new File("output.txt");
            BufferedWriter output = new BufferedWriter(new FileWriter(newTxtFile));

            while (line != null) {
                line = line.trim();
                if (line.matches(".*?\\bNStr\\b.*?") == true) { //Поиск ключевого слова в строке

                    if(tempLine!=""){

                        delimeter = ";";
                        tempLine = tempLine.replace("/n", "").replace("'", "").replaceAll("\"\\)?,?\\)?$", "");

                        String[] arrayLine = tempLine.split(delimeter);
                        String[] arrayNum  = tempNum.split(delimeter);

                        int index = 0;
                        for (String lineArray:
                             arrayLine) {
                            if(arrayNum.length == 1){
                                output.write(arrayNum[0]+lineArray.trim().replace("=", ":")+ System.lineSeparator());
                            }else{
                                output.write(arrayNum[index]+lineArray.trim().replace("=", ":")+ System.lineSeparator());
                            }

                        index++;
                        }

                        //output.write(tempLine + System.lineSeparator());
                        tempLine = "";
                        tempNum  = "";
                    }
                    patternR = "\"(.*?)\"\\)";
                    delimeter = "';";
                    String[] arrayLine = main.lineToArray(line, patternR, delimeter);
                    if (arrayLine != null) {
                        for (String strArrayLine :
                                arrayLine) {
                        String [] subStr = strArrayLine.split("=");
                        output.write(num+" : "+subStr[0].trim()+" : "+subStr[1].replace("\'", "").trim()+ System.lineSeparator());
                        }
                    }

                    else { //обработка неполных строк

                        if (line.substring(line.length() - 1).equals(";")) {//если не многострочное предложение обрабатываем по предыдущим правилам
                            patternR = "\"(.*?)\"";
                            delimeter = "';";
                            String[] arrayLine1 = main.lineToArray(line, patternR, delimeter);
                            if (arrayLine1 != null) {
                                for (String strArrayLine :
                                        arrayLine1) {
                                    String[] subStr = strArrayLine.split("=");
                                    output.write(num + " : " + subStr[0].trim() + " : " + subStr[1].replace("\'", "").trim() + System.lineSeparator());
                                }
                            }
                        } else { //если многострочное обрабатываем через временные переменные

                            Pattern patternPardLine = Pattern.compile("\"([^\"]*)");   //Забрать подстроку после первых  ковычек
                            Matcher matcherPartLine = patternPardLine.matcher(line);

                            if (matcherPartLine.find()) {
                                tempNum = tempNum + num + " : ";
                                tempLine = tempLine + matcherPartLine.group(1).trim();
                            }
                        }
                    }
                } else if (line.matches(".*\\| ?\\b.*?") == true && line.matches("^((?!SELECT|FROM|IN|WHERE).)*$") == true) { //Поиск строки с | и исключаем строки запроса

                    line = line.replace("|", "");
                    if (line.matches(".*?(\\bru\\b|\\bro\\b|\\ben\\b|\\bvi\\b).*") == true) { //поиск в строке ключевых строк
                        tempNum  = tempNum + num+": ;"+ num+": ";
                        tempLine = tempLine +line.trim();
                    } else {
                        tempNum  = tempNum + num+": ";
                        tempLine = tempLine +line.trim();
                    }
                }
                num++;
                line = reader.readLine();
            }

            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] lineToArray(String line, String patternRegex, String delimiter) {

        Pattern pattern = Pattern.compile(patternRegex);   //Забрать подстроку между кавычками "\"(.*?)\"\\)"
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String thisLine = matcher.group(1);
            String[] subStr = thisLine.split(delimiter);
            return subStr;
        } else {
            return null;
        }
    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
