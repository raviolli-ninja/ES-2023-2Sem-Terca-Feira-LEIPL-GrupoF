import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;

public class JAVAToHTML {

    private File outputFile;

    public JAVAToHTML(String fileType, File file) {

        if (fileType.equals("csv")) {
            outputFile = javaToHTMLCSV(file);
        } else {
            outputFile = javaToHtmlJSON(file);
        }
    }

    public File javaToHTMLCSV(File csvFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile));
             FileWriter writer = new FileWriter(outputFile)) {

            writer.write("<div class = \"table-schedule-main\"><table border>\n");

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                writer.write("<tr>");

                for (String field : currentLine.split(","))
                    writer.write("<td>" + field + "</td>");

                writer.write("</tr>\n");
            }

            writer.write("</table>\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public File javaToHtmlJSON(File JSONFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(JSONFile));
             FileWriter writer = new FileWriter(outputFile)) {

            writer.write("<div class=\"table-schedule-main\"><table border>\n");

            // Parse JSON array from file
            JSONArray jsonArray = new JSONArray(fileToString(JSONFile));

            // Generate table headers from first JSON object
            JSONObject firstObject = jsonArray.getJSONObject(0);
            writer.write("<tr>");
            Iterator<?> keys = firstObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                writer.write("<th>" + key + "</th>");
            }
            writer.write("</tr>\n");

            // Generate table rows from JSON objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                writer.write("<tr>");
                keys = object.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = object.getString(key);
                    writer.write("<td>" + value + "</td>");
                }
                writer.write("</tr>\n");
            }

            writer.write("</table></div>\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public static String fileToString(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        return content;
    }

    public File getOutputFile(){
        return outputFile;
    }

}