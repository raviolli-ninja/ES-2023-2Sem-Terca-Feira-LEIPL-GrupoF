import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/ES-2023-2Sem-Terca-Feira-LEIPL-GrupoF-1.0-SNAPSHOT/main.html")
public class FileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the content type of the uploaded file
        String contentType = request.getContentType();

        //If the content type is null, then the user has not uploaded a file
        if (contentType == null) {
            response.getWriter().write("No file uploaded");
        }

        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        File tempFile = File.createTempFile(fileName, null);

        // Send the response back to the client
        response.setContentType("text/html");

        if (contentType.equals("text/csv")) {
            //CSV TO TABLE
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {

                response.getWriter().write("<div class = \"table-schedule-main\"><table border>\n");

                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    response.getWriter().write("<tr>");

                    for (String field : currentLine.split(","))
                        response.getWriter().write("<td>" + field + "</td>");

                    response.getWriter().write("</tr>\n");
                }

                response.getWriter().write("</table>\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {

                response.getWriter().write("<div class=\"table-schedule-main\"><table border>\n");

                // Parse JSON array from file
                JSONArray jsonArray = new JSONArray(fileToString(tempFile));

                // Generate table headers from first JSON object
                JSONObject firstObject = jsonArray.getJSONObject(0);
                response.getWriter().write("<tr>");
                Iterator<?> keys = firstObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    response.getWriter().write("<th>" + key + "</th>");
                }
                response.getWriter().write("</tr>\n");

                // Generate table rows from JSON objects
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    response.getWriter().write("<tr>");
                    keys = object.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = object.getString(key);
                        response.getWriter().write("<td>" + value + "</td>");
                    }
                    response.getWriter().write("</tr>\n");
                }

                response.getWriter().write("</table></div>\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String fileToString(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        return content;
    }


}