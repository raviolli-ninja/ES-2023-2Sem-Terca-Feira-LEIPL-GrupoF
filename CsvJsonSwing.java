import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CsvJsonSwing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    private File lastLoadedFile; // Update the variable name to lastLoadedFile

    public CsvJsonSwing() {

        super("CSV/JSON to Table Swing Example");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(panel);

        // Table for schedule
        table = new JTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton loadButton = new JButton("Load File");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV/JSON Files", "csv", "json"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                lastLoadedFile = new File(filename); // Update the lastLoadedFile variable
                loadFile(filename);
            }
        });
        panel.add(loadButton, BorderLayout.SOUTH);

        JButton convertButton = new JButton("Convert to HTML");
        convertButton.addActionListener(e -> {
            if (lastLoadedFile != null) { // Update the variable name to lastLoadedFile
                try {
                    convertToHTML(lastLoadedFile.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error converting file to HTML: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No file has been loaded to convert.");
            }
        });
        panel.add(convertButton, BorderLayout.NORTH);

        JButton openButton = new JButton("Open Converted File");
        openButton.addActionListener(e -> {
            if (lastLoadedFile != null) { // Update the variable name to lastLoadedFile
                try {
                    Desktop.getDesktop().open(lastLoadedFile);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening converted file: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No converted file has been generated.");
            }
        });
        panel.add(openButton, BorderLayout.WEST);
    }


    public void loadFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            List<String[]> data = new ArrayList<>();
            String line;
            boolean isCsv = filename.endsWith(".csv");
            if (isCsv) {
                while ((line = br.readLine()) != null) {
                    String[] row = line.split(",");
                    data.add(row);
                }
            } else {
                JSONTokener tokener = new JSONTokener(new FileReader(filename));
                JSONArray jsonArray = new JSONArray(tokener);
                if (jsonArray.length() == 0) {
                    JOptionPane.showMessageDialog(this, "File is empty");
                    return;
                }
                JSONObject firstObject = jsonArray.getJSONObject(0);
                Iterator<?> keys = firstObject.keys();
                List<String> columnNames = new ArrayList<>();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    columnNames.add(key);
                }
                data.add(columnNames.toArray(new String[columnNames.size()]));
                for (int i = 0; i < jsonArray.length(); i++) {
                    List<String> row = new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    for (String columnName : columnNames) {
                        String value = jsonObject.optString(columnName);
                        row.add(value);
                    }
                    data.add(row.toArray(new String[row.size()]));
                }
            }
            DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][0]), data.get(0));
            table.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
        }
    }

    public void convertToHTML(String filename) throws IOException {
        String outputFilename = filename.replace(".csv", ".html").replace(".json", ".html");
        try (BufferedReader br = new BufferedReader(new FileReader(filename));
             FileWriter fw = new FileWriter(outputFilename)) {
            boolean isCsv = filename.endsWith(".csv");
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<html><body><table border='1'>");
            String line;
            if (isCsv) {
                while ((line = br.readLine()) != null) {
                    String[] row = line.split(",");
                    htmlTable.append("<tr>");
                    for (String cell : row) {
                        htmlTable.append("<td>").append(cell).append("</td>");
                    }
                    htmlTable.append("</tr>");
                }
            } else {
                JSONTokener tokener = new JSONTokener(new FileReader(filename));
                JSONArray jsonArray = new JSONArray(tokener);
                if (jsonArray.length() == 0) {
                    JOptionPane.showMessageDialog(this, "File is empty");
                    return;
                }
                JSONObject firstObject = jsonArray.getJSONObject(0);
                Iterator<?> keys = firstObject.keys();
                htmlTable.append("<tr>");
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    htmlTable.append("<th>").append(key).append("</th>");
                }
                htmlTable.append("</tr>");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    htmlTable.append("<tr>");
                    keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = jsonObject.getString(key);
                        htmlTable.append("<td>").append(value).append("</td>");
                    }
                    htmlTable.append("</tr>");
                }
            }
            htmlTable.append("</table></body></html>");
            fw.write(htmlTable.toString());
            JOptionPane.showMessageDialog(this, "File has been converted to HTML successfully.\nOutput file: "
                    + outputFilename);

            // Update the lastLoadedFile variable
            lastLoadedFile = new File(outputFilename);
        } catch (IOException e) {
            throw new IOException("Error converting file to HTML: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CsvJsonSwing test = new CsvJsonSwing();
            test.setVisible(true);
        });
    }
}