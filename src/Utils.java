package src;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


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








    public static void main(String[] args) throws IOException {
        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("ES","lei","segunda","b12",110,25,"12:30","15:00","11/12/2023","pl");
        Bloco bloco2 = new Bloco("ES","lei-pl","terça","b12",110,25,"12:30","15:00","11/12/2023","pl");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horarioToCSV(horario,"teste1csv");
    }

}
