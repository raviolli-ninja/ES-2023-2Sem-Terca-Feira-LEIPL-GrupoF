package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
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
/*
        String line;
        boolean isCsv = filename.endsWith(".csv");
        if (isCsv) {
            List<String[]> data = new ArrayList<>();
            while ((line = br.readLine()) != null) {


                //CSV Fix
                String[] row = line.split(",");
                data.add(row);
            }
        } else {
*/
            try {

                Horario horario = Utils.parseJson(filename);

                List<Object[]> data = new ArrayList<>();
                List<String> columnNames = new ArrayList<>();
                columnNames.add("uc");
                columnNames.add("turma");
                columnNames.add("curso");
                columnNames.add("dia_sem");
                columnNames.add("sala");
                columnNames.add("maxSala");
                columnNames.add("nInscritos");
                columnNames.add("horaInicioUC");
                columnNames.add("horaFimUC");
                columnNames.add("dataAula");
                columnNames.add("turno");
                data.add(columnNames.toArray(new String[columnNames.size()]));
                for (Bloco bloco : horario.horario) {
                    List<Object> row = new ArrayList<>();
                    row.add(bloco.getUc());
                    row.add(bloco.getTurma());
                    row.add(bloco.getCurso());
                    row.add(bloco.getDia_sem());
                    row.add(bloco.getSala());
                    row.add(bloco.getMaxSala());
                    row.add(bloco.getnInscritos());
                    row.add(bloco.getHoraInicioUC());
                    row.add(bloco.getHoraFimUC());
                    row.add(bloco.getDataAula());
                    row.add(bloco.getTurno());
                    data.add(row.toArray(new Object[row.size()]));
                }
                DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][0]), data.get(0));
                table.setModel(model);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        //}
    }

    public void convertToHTML(String filename) throws IOException {

        Horario horario = Utils.parseJson(filename);

        String outputFilename = filename.replace(".csv", ".html").replace(".json", ".html");


        try (BufferedReader br = new BufferedReader(new FileReader(filename));
             FileWriter fw = new FileWriter(outputFilename)) {
            boolean isCsv = filename.endsWith(".csv");
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<html><head><link rel=\"stylesheet\" href =\"styles.css\"><body><table border='1'>");
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

                // Create a new Horario instance
                /*
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Extract data from the JSON object
                    String uc = jsonObject.getString("uc");
                    String turma = jsonObject.getString("turma");
                    String dia_sem = jsonObject.getString("dia_sem");
                    String sala = jsonObject.getString("sala");
                    int maxSala = jsonObject.getInt("maxSala");
                    int nInscritos = jsonObject.getInt("nInscritos");
                    String horaInicioUC = jsonObject.getString("horaInicioUC");
                    String horaFimUC = jsonObject.getString("horaFimUC");
                    String dataAula = jsonObject.getString("dataAula");
                    String curso = jsonObject.optString("curso", "");

                    // Create a new Bloco instance and add it to the Horario object
                    Bloco bloco = new Bloco(uc, turma, dia_sem, sala, maxSala, nInscritos, horaInicioUC, horaFimUC, dataAula, curso);
                    horario.addToHor(bloco);
                }
                */


                // Generate HTML table from Horario object
                htmlTable = new StringBuilder("<html><head><title>" + horario.nome + "</title></head><body><table>");

                if (horario.horario.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No data available");
                    return;
                }

                // Add table headers
                Bloco firstBloco = horario.horario.get(0);
                Field[] fields = firstBloco.getClass().getDeclaredFields();
                htmlTable.append("<tr>");
                for (Field field : fields) {
                    htmlTable.append("<th>").append(field.getName()).append("</th>");
                }
                htmlTable.append("</tr>");

                // Add table data
                for (Bloco bloco : horario.horario) {
                    htmlTable.append("<tr>");
                    for (Field field : fields) {
                        String value;
                        try {
                            Object fieldValue = field.get(bloco);
                            value = fieldValue == null ? "" : fieldValue.toString();
                        } catch (IllegalAccessException e) {
                            value = "";
                        }
                        htmlTable.append("<td>").append(value).append("</td>");
                    }
                    htmlTable.append("</tr>");
                }


                htmlTable.append("</table></body></html>");
                fw.write(htmlTable.toString());
                JOptionPane.showMessageDialog(this, "File has been converted to HTML successfully.\nOutput file: "
                        + outputFilename);

                // Update the lastLoadedFile variable
                lastLoadedFile = new File(outputFilename);
            }
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