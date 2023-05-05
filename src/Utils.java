package src;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class Utils {


    public static void horarioTOJSON(Horario horario, String nomeArquivo) throws IOException {
        // Cria o objeto que irá representar o horário em JSON
        HorarioJSON horarioJSON = new HorarioJSON();
        horarioJSON.setHorarios(horario.horario);

        // Configura o Gson para imprimir o JSON formatado
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Converte o objeto para JSON
        String json = gson.toJson(horarioJSON);

        // Salva o JSON no arquivo
        try (FileWriter file = new FileWriter(nomeArquivo+".json")) {
            file.write(json);
        }
    }

    private static class HorarioJSON {
        private ArrayList<Bloco> horarios;

        public ArrayList<Bloco> getHorarios() {
            return horarios;
        }

        public void setHorarios(ArrayList<Bloco> horarios) {
            this.horarios = horarios;
        }
    }

    public static void horarioToCSV(Horario horario, String nomeArquivo) throws IOException {
        // Cria o FileWriter para escrever no arquivo
        FileWriter writer = new FileWriter(nomeArquivo+".csv");

        // Escreve o cabeçalho no arquivo
        writer.append("UC,Turma,Dia,Sala,Curso,Turno,Inicio,Fim,DataAula,MaxSala,Inscritos\n");

        // Percorre todos os blocos do horário e os escreve no arquivo
        for (Bloco bloco : horario.horario) {
            writer.append(bloco.getUc());
            writer.append(",");
            writer.append(bloco.getTurma());
            writer.append(",");
            writer.append(bloco.getDia_sem());
            writer.append(",");
            writer.append(bloco.getSala());
            writer.append(",");
            writer.append(bloco.getCurso());
            writer.append(",");
            writer.append(bloco.getTurno());
            writer.append(",");
            writer.append(bloco.getHoraInicioUC());
            writer.append(",");
            writer.append(bloco.getHoraFimUC());
            writer.append(",");
            writer.append(bloco.getDataAula());
            writer.append(",");
            writer.append(Integer.toString(bloco.getMaxSala()));
            writer.append(",");
            writer.append(Integer.toString(bloco.getnInscritos()));
            writer.append("\n");
        }

        // Fecha o FileWriter
        writer.close();
    }

    public static Horario csvToHorario(String arquivoCSV) throws IOException {
        Horario horario = new Horario();
        BufferedReader br = new BufferedReader(new FileReader(arquivoCSV));
        String linha = br.readLine(); // pula a linha de cabeçalho do arquivo
        while ((linha = br.readLine()) != null) {
            String[] campos = linha.split(",");

            String curso = campos[0];
            String uc = campos[1];
            String turma = campos[3];
            String diaSem = campos[5];
            String sala = campos[9];
            int maxSala = Integer.parseInt(campos[10]);
            int nInscritos = Integer.parseInt(campos[4]);
            String horaInicioUC = campos[6];
            String horaFimUC = campos[7];
            String dataAula = campos[8];
            String turno = campos[2];

            Bloco bloco = new Bloco(curso,turno, uc,  turma,  diaSem, sala,  maxSala,nInscritos, horaInicioUC, horaFimUC, dataAula);
            horario.addToHor(bloco);
        }

        br.close();

        return horario;
    }


    public static Horario parseJson(String filename) {
        Gson gson = new Gson();
        Horario horario = new Horario();
        try {
            // Lê o arquivo JSON e converte em um objeto JsonObject
            FileReader fileReader = new FileReader(filename);
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);

            // Extrai a lista de blocos do objeto JsonObject
            JsonArray jsonArray = jsonObject.get("horarios").getAsJsonArray();

            // Itera sobre cada elemento do JsonArray e cria um objeto Bloco para cada um
            ArrayList<Bloco> blocos = new ArrayList<Bloco>();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonBloco = jsonElement.getAsJsonObject();
                Bloco bloco = new Bloco();
                bloco.setUc(jsonBloco.get("uc").getAsString());
                bloco.setTurma(jsonBloco.get("turma").getAsString());
                bloco.setDia_sem(jsonBloco.get("dia_sem").getAsString());
                bloco.setSala(jsonBloco.get("sala").getAsString());
                bloco.setCurso(jsonBloco.get("curso").getAsString());
                bloco.setTurno(jsonBloco.get("turno").getAsString());
                bloco.setHoraInicioUC(jsonBloco.get("horaInicioUC").getAsString());
                bloco.setHoraFimUC(jsonBloco.get("horaFimUC").getAsString());
                bloco.setDataAula(jsonBloco.get("dataAula").getAsString());
                bloco.setMaxSala(jsonBloco.get("maxSala").getAsInt());
                bloco.setnInscritos(jsonBloco.get("nInscritos").getAsInt());
                horario.addToHor(bloco);
            }


            return horario;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /***********************************************************************************
     *
     * Engine para o display
     */

    private void sortByDate(Horario horario) {
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
    public ArrayList getOverCrowded(Horario horario){
        ArrayList<Integer> index = new ArrayList<Integer>();
        for (Bloco bloco : horario.horario) {
            if (bloco.getMaxSala()< bloco.nInscritos){
                index.add(horario.horario.indexOf(bloco));
            }
        }
        return index;
    }

    public void setOverCrowded(Horario horario , JTable table) {
        sortByDate(horario);
        ArrayList<Integer> indexList =getOverCrowded(horario);

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

    public ArrayList getOverlap (Horario horario){
        ArrayList<Integer> index = new ArrayList<Integer>() ;
        ArrayList<Bloco> blocos = horario.horario;

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

    public void setOverlap(Horario horario , JTable table) {
        sortByDate(horario);
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

    public Horario filter(Horario horario, String  filter, String date){
        Horario filtred = new Horario();
        int[] day31 = {1,3,5,7,8,10,12};
        switch (filter){
            case "day":
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                Date dataAula;
                try {
                    dataAula = dateFormat.parse(date);
                } catch ( ParseException e) {
                    // Se a data não estiver no formato esperado, retorna null
                    return null;
                }



                // Percorre os blocos do horário e adiciona aqueles que estão na data desejada ao novo horário
                for (Bloco bloco : horario.horario) {
                    if (bloco.getDataAula().equals(dataAula)) {
                        filtred.addToHor(bloco);
                    }
                }

                break;
            case "week":
                String firstday = getFday(date, horario,day31);
                String lastday = getLday(firstday,horario,day31);
                for (Bloco bloco : horario.horario) {
                    if (bloco.getDataAula().compareTo(firstday) >= 0 && bloco.getDataAula().compareTo(lastday) <= 0) {
                        filtred.addToHor(bloco);
                    }
                }


                break;
            case "month":
                String[] parseData = date.split("/");
                for (Bloco bloco : horario.horario) {
                    String dateHor = bloco.getDataAula();
                    String[] parsehor = dateHor.split("/");
                    if(parsehor[1].equals(parseData[1])){
                        filtred.addToHor(bloco);
                    }
                }

                break;


        }
        return  filtred;




    }

    String getFday(String date, Horario horario, int[] array){
        String[] parsedate = date.split("/");
        int day = Integer.parseInt(parsedate[0]);
        int month = Integer.parseInt(parsedate[1]);

        String weekDay = "null" ;
        for (Bloco bloco : horario.horario) {
            if (date == bloco.getDataAula());
            weekDay = bloco.getDia_sem();
            break;
        }
        switch (weekDay) {
            case "segunda":
            case "segunda-feira":
                day--;
                break;
            case "terça":
            case "terça-feira":
            case "terca":
            case "terca-feira":
                day= day-2;
                break;
            case "quarta":
            case "quarta-feira":
                day= day-3;
                break;
            case "quinta":
            case "quinta-feira":
                day= day-4;
                break;
            case "sexta":
            case "sexta-feira":
                day= day-5;
                break;
            case "sábado":
            case "sabado":
                day= day-6;
                break;
            case "domingo":
                day= day-7;
                break;
            default:

                break;
        }

        if (day<1){
            month--;
            if(month <1){
                month= 12;
            }
            if(month == 2){
                day = 28 -4;
            }else{
                if(check31(month,array)){
                    day= 31-4;
                }else {
                    day = 30-4;
                }
            }

        }

        return (day +"/" + month+ "/"+parsedate[2]);




    }
    String getLday(String date, Horario horario, int[] array){
        String[] parsedate = date.split("/");
        int day = Integer.parseInt(parsedate[0]);
        int month = Integer.parseInt(parsedate[1]);
        day += 7;
        if(check31(month,array)){
            if (day>31){
                month++;
                day = day-31;
            }
        }else {
            if (day>30){
                month++;
                day = day-30;
            }
        }
        return (day +"/" + month+ "/"+parsedate[2]);

    }
    boolean check31(int month, int[] array){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == month) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("ES","lei","segunda","b12",110,25,"12:30","15:00","11/12/2023","pl");
        Bloco bloco2 = new Bloco("ES","lei-pl","terça","b12",110,25,"12:30","15:00","11/12/2023","pl");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horarioToCSV(horario,"teste1csv");
    }

}
