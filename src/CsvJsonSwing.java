package src;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.Component;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CsvJsonSwing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    private File lastLoadedFile; // Update para lastLoadedFile para ser mais claro

    private JPanel panel;

    //Se houver mais de 1 panel, é necessário criar um subPanel array?
    private JPanel subPanel;

    private JComboBox<String> sortComboBox;

    Horario horario;

    String source ;

    public CsvJsonSwing()  {

        super("ISCTE Schedule Generator");

        panelRequirement();

        //SubPanel para os butões no BorderLayout.SOUTH, no panel do panelRequirement()
        subPanel = new JPanel();

        //Não confundir com a table com model, isto apenas abre espaço para o model
        showPreviewTable();

       //Butons
        loadFileButton();
        convertHTMLButton();
        openConvertedFileButton();
        loadFromURIButton();
        setOverlapButton();
        setOvercrowdingButton();


        //Sorting
        String[] sortOptions = {"Sort by Day", "Sort by Week", "Sort by Month"};
        sortComboBox = new JComboBox<>(sortOptions);
        setSortComboBoxWindow();

    }
    public void loadFile(String filename) throws IOException {
        source = filename;
        boolean isCsv = filename.endsWith(".csv");
        if (isCsv) {
            horario = Utils.csvToHorario(filename);
        }
        else {
            horario = Utils.parseJson(filename);
        }
        DefaultTableModel model = getModel();
        displayModel(model);


    }

    public DefaultTableModel getModel(){
        List<Object[]> data = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        //Método auxiliar para adicionar colunas
        columnAdder((ArrayList<String>) columnNames);


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
        model.removeRow(0);
       return model;
    }

    public void displayModel(DefaultTableModel model){
        table.setModel(model);
    }










    public void loadFileFromUri(String uri) throws IOException, ParserException {
        source = uri;
//        try {
            if (uri.startsWith("https://")) {
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(new URL(uri).openStream());
                List<String[]> data = new ArrayList<>();
                for (Object event : calendar.getComponents(Component.VEVENT)) {
                    String[] fields = new String[4];
                    fields[0] = ((VEvent) event).getSummary().getValue();
                    fields[1] = ((VEvent) event).getStartDate().getValue().toString();
                    fields[2] = ((VEvent) event).getEndDate().getValue().toString();
                    fields[3] = ((VEvent) event).getDescription().toString();
                    data.add(fields);
                }
                String[] headers = {"UC", "Start Date (Format: yy/dd/mm)", "End Date (Format: yy/dd/mm)", "Summary"};
                DefaultTableModel model = new DefaultTableModel(headers, 0);
                for (String[] row : data) {
                    model.addRow(row);
                }
                table.setModel(model);
            } else {
                JOptionPane.showMessageDialog(this,"Invalid Protocol. Only https:// is supported. If using webcal link please change the start to https://. Webcal is not secured and is not supported by the IANA.");
            }
//        } catch (MalformedURLException e) {
//            JOptionPane.showMessageDialog(this, "Invalid URI: " + uri);
//            e.printStackTrace();
//        } catch (IOException | ParserException e) {
//            JOptionPane.showMessageDialog(this, "Error loading file from URI: " + e.getMessage());
//            e.printStackTrace();
//        }
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



    /********************************************************************
     * TIRAR DAQUI                                                      *
     ********************************************************************/

    public ArrayList getOverCrowded(){
        ArrayList<Integer> index = new ArrayList<Integer>();
        for (Bloco bloco : horario.horario) {
            if (bloco.getMaxSala()< bloco.nInscritos){
                index.add(horario.horario.indexOf(bloco));
            }
        }
       return index;
    }

    public ArrayList getOverlap (Horario horario){
        ArrayList<Integer> index = new ArrayList<Integer>() ;
        List<Bloco> blocos = horario.horario;

        for (int i = 0; i < blocos.size(); i++) {
            Bloco bloco1 = blocos.get(i);
            for (int j = i + 1; j < blocos.size(); j++) {
                Bloco bloco2 = blocos.get(j);
                if (bloco1.getDataAula().equals(bloco2.getDataAula()) &&
                        bloco1.getHoraInicioUC().equals(bloco2.getHoraInicioUC()) &&
                        bloco1.getSala().equals(bloco2.getSala())) {
                    index.add(blocos.indexOf(bloco1));
                    index.add(blocos.indexOf(bloco2));
                }
            }
        }

        return index;
    }


    public void setOverlap() {
        sortByDate();
        ArrayList<Integer> indexList = getOverlap(horario);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(Color.RED);

        for (int i = 0; i < indexList.size(); i++) {
            int rowIndex = indexList.get(i);
            for (int j = 0; j < table.getColumnCount(); j++) {
                table.getCellRenderer(rowIndex, j).getTableCellRendererComponent(table, table.getValueAt(rowIndex, j), false, false, rowIndex, j).setBackground(Color.RED);
            }
            model.fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    public void setOvercrowding() {
        sortByDate();
        ArrayList<Integer> indexList =getOverCrowded();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(Color.BLUE);

        for (int i = 0; i < indexList.size(); i++) {
            int rowIndex = indexList.get(i);
            for (int j = 0; j < table.getColumnCount(); j++) {
                table.getCellRenderer(rowIndex, j).getTableCellRendererComponent(table, table.getValueAt(rowIndex, j), false, false, rowIndex, j).setBackground(new Color(51,153,255));
            }
            model.fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    private void sortByDate() {
        ArrayList<Bloco> blocos = horario.horario;
        Collections.sort(blocos, new Comparator<Bloco>() {
            public int compare(Bloco b1, Bloco b2) {
                int dataComp = b1.getDataAula().compareTo(b2.getDataAula());
                if (dataComp == 0) {
                    int horaComp = b1.getHoraInicioUC().compareTo(b2.getHoraInicioUC());
                    return horaComp;
                }
                return dataComp;
            }
        });

    }

    /*public  DefaultTableModel tableTOModel(){
        TableModel model =table.getModel();
        DefaultTableModel defaultModel = new DefaultTableModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
           defaultModel.addColumn(model.getColumnName(i));
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }
            defaultModel.addRow(row);
        }

        return defaultModel;

    }*/











    /********************************************************************
     * SWING CODE (functions for GUI, auxiliary functincs, buttons, etc)*
     ********************************************************************/

    public void panelRequirement(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(panel);
    }

    public void showPreviewTable(){
        table = new JTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
    }


    public void loadFileButton() {
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
    }

    public void setSortComboBoxWindow(){
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
    }

    public void convertHTMLButton(){

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
    }

    public void openConvertedFileButton(){
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


    public void loadFromURIButton(){
        JButton loadFromUriButton = new JButton("Load from URI");
        loadFromUriButton.addActionListener(e -> {
            String uri = JOptionPane.showInputDialog(this, "Enter URI (use https:// instead of webcal:// for security reasons):");
            if (uri != null) {
                loadFileFromUri(uri);
            }else{
                JOptionPane.showMessageDialog(this, "Null input, no preview generated.");
            }
        });

        subPanel.add(loadFromUriButton);
        panel.add(subPanel, BorderLayout.SOUTH);

    }
    public void setOverlapButton(){
        JButton loadFromUriButton = new JButton("Aulas sobrepostas");
        loadFromUriButton.addActionListener(e -> {
           setOverlap();
        });

        subPanel.add(loadFromUriButton);
        panel.add(subPanel, BorderLayout.SOUTH);

    }
    public void setOvercrowdingButton(){
        JButton loadFromUriButton = new JButton("Aulas Sobrelotadas");
        loadFromUriButton.addActionListener(e -> {
            setOvercrowding();
        });

        subPanel.add(loadFromUriButton);
        panel.add(subPanel, BorderLayout.SOUTH);

    }


    /********************************************************************
     * Auxiliary Functions                                              *
     ********************************************************************/

    public void columnAdder(ArrayList<String> columnNames){
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
    }















    //Para mostrar que http não funciona pois o link é movido para https e altera o conteudo para html e o parser do iCalendar não funciona
    //Webcal também não funciona pois não é um protocolo reconhecido pela IANA logo dava o erro de URLMalformation e tinha de usar várias libraries para contornar isso e ainda por cima era depois traduzido para HTTPS
    //logo não faz sentido usar webcal sequer.
    /*private void loadFileFromUri2(String uri) {
        try {
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            if (uri.startsWith("webcal://")) {
                String httpUrl = connection.getHeaderField("Location");
                connection = new URL(httpUrl).openConnection();
            }
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            List<String[]> data = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                data.add(fields);
            }
            reader.close();
            inputStream.close();

            String[] headers = data.remove(0);
            DefaultTableModel model = new DefaultTableModel(headers, 0);
            for (String[] row : data) {
                model.addRow(row);
            }
            table.setModel(model);

        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(this, "Invalid URI: " + uri);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file from URI: " + e.getMessage());
        }
    }
    */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CsvJsonSwing test = new CsvJsonSwing();
            test.setVisible(true);
        });
    }
}