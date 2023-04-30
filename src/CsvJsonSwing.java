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
                try {
                    loadFile(filename);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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


    public void loadFile(String filename) throws IOException {

        String line;
        boolean isCsv = filename.endsWith(".csv");
        if (isCsv) {
            FileReader fileReader = new FileReader(filename);
            Horario horario = Utils.csvToHorario(filename);
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
        } else {

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
        }
    }

    public void convertToHTML(String filename) throws IOException {

        String outputFilename = filename.replace(".csv", ".html").replace(".json", ".html");


        try (BufferedReader br = new BufferedReader(new FileReader(filename));
             FileWriter fw = new FileWriter(outputFilename)) {
            boolean isCsv = filename.endsWith(".csv");
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<html><head><link rel=\"stylesheet\" href =\"styles.css\"><body><table border='1'>");
            String line;
            if (isCsv) {

                Horario horario = Utils.csvToHorario(filename);

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
                lastLoadedFile = new File(outputFilename);

            } else {

                Horario horario = Utils.parseJson(filename);

                // Generate HTML table from Horario object
                htmlTable = new StringBuilder("<html><head><link rel=\"stylesheet\" href =\"styles.css\"></head><title>" + horario.nome + "</title><body><table border='1'>");

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