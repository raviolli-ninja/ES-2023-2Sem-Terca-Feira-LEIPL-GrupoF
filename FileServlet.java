import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        JAVAToHTML javaToHTML;

        if (contentType == null) {
            response.getWriter().write("No file uploaded");
        }

        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        File tempFile = File.createTempFile(fileName, null);

        try(InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }


        // Check if the content type indicates a CSV file
        if (contentType.equals("text/csv")) {
            javaToHTML = new JAVAToHTML("csv", tempFile);
        } else {
            // The uploaded file is not a CSV file
            javaToHTML = new JAVAToHTML("json", tempFile);
        }

        File outputFile = javaToHTML.getOutputFile();

        // Send the response back to the client
        response.setContentType("text/plain");
        response.getWriter().write(JAVAToHTML.fileToString(outputFile));
    }
}