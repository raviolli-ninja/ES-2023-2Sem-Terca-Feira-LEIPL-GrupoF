import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/process-file")
public class FileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the content type of the uploaded file
        String contentType = request.getContentType();
        JAVAToHTML javaToHTML;

        if (contentType == null || contentType == " ") {
            response.getWriter().write("No file uploaded");
        }

        // Check if the content type indicates a CSV file
        if (contentType.equals("text/csv")) {
            javaToHTML = JAVAToHTML("csv", request.getPart("file").getInputStream());
        } else {
            // The uploaded file is not a CSV file
            javaToHTML = JAVAToHTML("json", request.getPart("file").getInputStream());
        }

        // Send the response back to the client
        response.setContentType("text/html");
        response.getWriter().write(fileToString(javaToHTML.getOutputFile()));
    }
}