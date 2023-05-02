package src.testes;

import org.testng.annotations.Test;
import src.Bloco;
import src.Horario;
import src.Utils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static src.Utils.readFile;

class UtilsTest {

    @org.junit.jupiter.api.Test
    public void testHorarioTOJSON() throws IOException {
        // Cria um objeto de Horario com alguns blocos
        Horario horario = new Horario("Horario de teste");
        horario.addToHor(new Bloco("Curso", "Turno", "UC1", "Turma", "Segunda", "Sala1", 50, 30, "08:00", "10:00", "01/01/2023"));
        horario.addToHor(new Bloco("Curso", "Turno", "UC2", "Turma", "Terça", "Sala2", 30, 15, "10:00", "12:00", "02/01/2023"));

        // Chama o método horarioTOJSON para gerar o arquivo JSON
        Utils.horarioTOJSON(horario, "horario_teste");

        // Verifica se o arquivo foi criado
        File arquivo = new File("horario_teste.json");
        assertTrue(arquivo.exists());

        // Deleta o arquivo para não deixar resíduos no sistema de arquivos
        arquivo.delete();
    }




    @Test
    void horarioToCSV() throws IOException {
        // Cria um novo Horario

        Horario horario = new Horario("Horario de teste");
        horario.addToHor(new Bloco("Curso", "Turno", "UC1", "Turma", "Segunda", "Sala1", 50, 30, "08:00", "10:00", "01/01/2023"));
        horario.addToHor(new Bloco("Curso", "Turno", "UC2", "Turma", "Terça", "Sala2", 30, 15, "10:00", "12:00", "02/01/2023"));


        // Testa se o arquivo CSV é criado corretamente
        try {
            Utils.horarioToCSV(horario, "horario_teste");
            assertTrue(true);
        } catch (IOException e) {
            fail("Não deveria ter lançado exceção: " + e.getMessage());
        }

        // Testa se o arquivo CSV contém as informações corretas
        String csvContent = "UC,Turma,Dia,Sala,Curso,Turno,Inicio,Fim,DataAula,MaxSala,Inscritos\n" +
                "UC1,Turma,Segunda,Sala1,Curso,Turno,08:00,10:00,01/01/2023,50,30\n" +
                "UC2,Turma,Terça,Sala2,Curso,Turno,10:00,12:00,02/01/2023,30,15\n";
        assertEquals(csvContent, readFile("horario_teste.csv"));

        // Apaga o arquivo criado
        Utils.deleteFile("horario_teste.csv");
    }


    @org.junit.jupiter.api.Test
    void csvToHorario() {
    }

    @org.junit.jupiter.api.Test
    void parseJson() {
    }

    @org.junit.jupiter.api.Test
    void main() {
    }
}