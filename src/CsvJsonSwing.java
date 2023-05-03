package src;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class CsvJsonSwing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    private File lastLoadedFile; // Update the variable name to lastLoadedFile


    public CsvJsonSwing() {

        super("ISCTE Schedule Generator");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(panel);
        JPanel subPanel = new JPanel();
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
        subPanel.add(loadButton);

        //SORTING BOX
        String[] sortOptions = {"Sort by Day", "Sort by Week", "Sort by Month"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        JButton convertButton = new JButton("Convert to HTML");
        convertButton.addActionListener(e -> {
            if (lastLoadedFile != null) { // Update the variable name to lastLoadedFile
                try {
                    convertToHTML(lastLoadedFile.getAbsolutePath(), sortComboBox.getSelectedIndex());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error converting file to HTML: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No file has been loaded to convert.");
            }
        });
        panel.add(convertButton, BorderLayout.EAST);

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


        sortComboBox.addActionListener(e -> {
            if (lastLoadedFile != null) {
                try {
                    sortTable(sortComboBox.getSelectedIndex());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error sorting table: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No file has been loaded to sort.");
            }
        });
        panel.add(sortComboBox, BorderLayout.NORTH);

        JButton loadFromUriButton = new JButton("Load from URI");
        loadFromUriButton.addActionListener(e -> {
            String uri = JOptionPane.showInputDialog(this, "Enter URI:");
            if (uri != null) {
                loadFileFromUri(uri);
            }else{
                JOptionPane.showMessageDialog(this, "Null input.");
            }
        });

        subPanel.add(loadFromUriButton);
        panel.add(subPanel, BorderLayout.SOUTH);

    }

    private void loadFileFromUri(String uri) {








    }

    public void sortTable(int sortOption) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 1; i < model.getRowCount(); i++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }
            rows.add(row);
        }
        model.setRowCount(0);

        int dataAulaColumnIndex = model.findColumn("dataAula");


        Comparator<LocalDateTime> comparator = null;
        switch (sortOption) {
            case 0: // Day
                comparator = Comparator.comparing(LocalDateTime::toLocalDate);
                break;
            case 1: // Week
                comparator = Comparator.comparing((LocalDateTime dateTime) -> dateTime.toLocalDate().with(DayOfWeek.MONDAY));
                break;
            case 2: // Month
                comparator = Comparator.comparing((LocalDateTime dateTime) -> YearMonth.from(dateTime));
                break;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Comparator<LocalDate> finalComparator = comparator != null ? Comparator.comparing(LocalDate::from) : null;
        if (finalComparator == null) {
            // Handle error case where sortOption is invalid
            return;
        }

        List<Object[]> rowsCopy = new ArrayList<>(rows);
        rowsCopy.sort((a, b) -> {
            LocalDate aDate = LocalDate.parse((String) a[dataAulaColumnIndex], dateFormatter);
            LocalDate bDate = LocalDate.parse((String) b[dataAulaColumnIndex], dateFormatter);
            return finalComparator.compare(aDate, bDate);
        });

        model.setRowCount(0);
        for (Object[] row : rowsCopy) {
            model.addRow(row);
        }

    }


    private int getWeekNumber(int year, int month, int day) {
        LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
        return date.getDayOfYear() / 7 + 1;
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


    private void convertToHTML(String filename, int sortOption) throws IOException {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }
            rows.add(row);
        }

        int dataAulaColumnIndex = model.findColumn("dataAula");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Create a comparator based on the selected sorting option
        Comparator<Object[]> comparator = null;
        switch (sortOption) {
            case 0: // Sort by Day
                comparator = (a, b) -> {
                    LocalDate aDate = LocalDate.parse((String) a[dataAulaColumnIndex], dateFormatter);
                    LocalDate bDate = LocalDate.parse((String) b[dataAulaColumnIndex], dateFormatter);
                    return aDate.compareTo(bDate);
                };
                break;
            case 1: // Sort by Week
                comparator = (a, b) -> {
                    LocalDate aDate = LocalDate.parse((String) a[dataAulaColumnIndex], dateFormatter);
                    LocalDate bDate = LocalDate.parse((String) b[dataAulaColumnIndex], dateFormatter);
                    LocalDate aWeek = aDate.with(DayOfWeek.MONDAY);
                    LocalDate bWeek = bDate.with(DayOfWeek.MONDAY);
                    return aWeek.compareTo(bWeek);
                };
                break;
            case 2: // Sort by Month
                comparator = (a, b) -> {
                    LocalDate aDate = LocalDate.parse((String) a[dataAulaColumnIndex], dateFormatter);
                    LocalDate bDate = LocalDate.parse((String) b[dataAulaColumnIndex], dateFormatter);
                    YearMonth aMonth = YearMonth.from(aDate);
                    YearMonth bMonth = YearMonth.from(bDate);
                    return aMonth.compareTo(bMonth);
                };
                break;
        }

        // Sort the rows using the comparator
        rows.sort(comparator);

        // Write the sorted data to the HTML file
        String html = "<html><head>\n" +
                "  <link rel=\"stylesheet\" href=\"styles.css\">\n" +
                "</head><body><table border='1'>";

        for (int i = 0; i < model.getColumnCount(); i++) {
            html += "<th>" + model.getColumnName(i) + "</th>";
        }
        for (int i = 0; i < rows.size(); i++) {
            html += "<tr>";
            Object[] row = rows.get(i);
            for (int j = 0; j < row.length; j++) {
                html += "<td>" + row[j] + "</td>";
            }
            html += "</tr>";
        }
        html += "</table></body></html>";

        String filenameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
        Path path = Paths.get(filenameWithoutExtension + ".html");
        lastLoadedFile = new File(path.toString());
        Files.write(path, html.getBytes());
        JOptionPane.showMessageDialog(this, "File converted successfully: " + path.toAbsolutePath());
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CsvJsonSwing test = new CsvJsonSwing();
            test.setVisible(true);
        });
    }
}