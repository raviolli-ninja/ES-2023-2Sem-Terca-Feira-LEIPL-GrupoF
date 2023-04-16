package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;

public class CSVToJsonConverter {

    public static void main(String[] args) {
        //Curso,UC,Turno,Turma,Inscritos no turno,Dia da semana,Hora inicio da aula,Hora fim da aula,Data da aula,Sala atribuida,Lotacao da sala
        Horario horario = new Horario("horario-exemplo");

        try (BufferedReader br = new BufferedReader(new FileReader("horario-exemplo.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String curso = values[0];
                String UC = values [1];
                String turno = values[2];
                String turma = values[3];
                int nInscritos = Integer.parseInt(values[4]);
                String dia_sem = values[5];
                String horaInicioUC = values[6];
                String horaFimUC = values [7];
                String dataAula = values [8];
                String sala = values[9];
                int maxsala = Integer.parseInt(values[10]);
                //Block(String uc, String turma, String dia_sem, String sala, int maxSala, int nInscritos, String horaInicioUC, String horaFimUC, String dataAula)
                Bloco bloco = new Bloco(UC, turma, dia_sem, sala, maxsala, nInscritos, horaInicioUC, horaFimUC, dataAula);
                System.out.println(bloco);
                horario.addToHor(bloco);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
