package src.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static Utils.check31;
import static Utils.readFile;

class UtilsTest {

    @Test
    public void testHorarioTOJSON() throws IOException {
        // Cria um objeto de src.test.Horario com alguns blocos
        Horario horario = new Horario("src.test.Horario de teste");
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
        // Cria um novo src.test.Horario

        Horario horario = new Horario("src.test.Horario de teste");
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

    @Test
    void csvToHorarioTest() throws IOException {
        String arquivoCSV = "horario_teste.csv";
        Horario horario = Utils.csvToHorario(arquivoCSV);

        // Verifica se o número de blocos do horário é o esperado
        assertEquals(2, horario.horario.size());

        // Verifica se os dados do primeiro bloco estão corretos
        Bloco bloco1 = horario.horario.get(0);
        assertEquals("Curso", bloco1.getCurso());
        assertEquals("Turno", bloco1.getTurno());
        assertEquals("UC1", bloco1.getUc());
        assertEquals("Turma", bloco1.getTurma());
        assertEquals("Segunda", bloco1.getDia_sem());
        assertEquals("Sala1", bloco1.getSala());
        assertEquals(50, bloco1.getMaxSala());
        assertEquals(30, bloco1.getnInscritos());
        assertEquals("08:00", bloco1.getHoraInicioUC());
        assertEquals("10:00", bloco1.getHoraFimUC());
        assertEquals("01/01/2023", bloco1.getDataAula());

        // Verifica se os dados do segundo bloco estão corretos
        Bloco bloco2 = horario.horario.get(1);
        assertEquals("Curso", bloco2.getCurso());
        assertEquals("Turno", bloco2.getTurno());
        assertEquals("UC2", bloco2.getUc());
        assertEquals("Turma", bloco2.getTurma());
        assertEquals("Terça", bloco2.getDia_sem());
        assertEquals("Sala2", bloco2.getSala());
        assertEquals(40, bloco2.getMaxSala());
        assertEquals(15, bloco2.getnInscritos());
        assertEquals("10:00", bloco2.getHoraInicioUC());
        assertEquals("12:00", bloco2.getHoraFimUC());
        assertEquals("02/01/2023", bloco2.getDataAula());
    }

    @Test
    void parseJson() {
        // Criar um arquivo de teste JSON
        String jsonStr = "{\"horarios\":[{\"uc\":\"mat\",\"turma\":\"A1\",\"dia_sem\":\"Seg\",\"sala\":\"A101\",\"curso\":\"Eng. Civil\",\"turno\":\"Manhã\",\"horaInicioUC\":\"08:00\",\"horaFimUC\":\"10:00\",\"dataAula\":\"01/02/2023\",\"maxSala\":40,\"nInscritos\":30},{\"uc\":\"fis\",\"turma\":\"B1\",\"dia_sem\":\"Ter\",\"sala\":\"B201\",\"curso\":\"Eng. Elétrica\",\"turno\":\"Noite\",\"horaInicioUC\":\"19:00\",\"horaFimUC\":\"21:00\",\"dataAula\":\"02/02/2023\",\"maxSala\":50,\"nInscritos\":45}]}";
        String filename = "test.json";
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(jsonStr);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Executar o método parseJson com o arquivo de teste
        Horario horario = Utils.parseJson(filename);

        // Verificar se o objeto src.test.Horario contém a lista de blocos esperada
        assertEquals(2, horario.horario.size());
        Bloco bloco1 = horario.horario.get(0);
        assertEquals("mat", bloco1.getUc());
        assertEquals("A1", bloco1.getTurma());
        assertEquals("Seg", bloco1.getDia_sem());
        assertEquals("A101", bloco1.getSala());
        assertEquals("Eng. Civil", bloco1.getCurso());
        assertEquals("Manhã", bloco1.getTurno());
        assertEquals("08:00", bloco1.getHoraInicioUC());
        assertEquals("10:00", bloco1.getHoraFimUC());
        assertEquals("01/02/2023", bloco1.getDataAula());
        assertEquals(40, bloco1.getMaxSala());
        assertEquals(30, bloco1.getnInscritos());
        Bloco bloco2 = horario.horario.get(1);
        assertEquals("fis", bloco2.getUc());
        assertEquals("B1", bloco2.getTurma());
        assertEquals("Ter", bloco2.getDia_sem());
        assertEquals("B201", bloco2.getSala());
        assertEquals("Eng. Elétrica", bloco2.getCurso());
        assertEquals("Noite", bloco2.getTurno());
        assertEquals("19:00", bloco2.getHoraInicioUC());
        assertEquals("21:00", bloco2.getHoraFimUC());
        assertEquals("02/02/2023", bloco2.getDataAula());
        assertEquals(50, bloco2.getMaxSala());
        assertEquals(45, bloco2.getnInscritos());

        // Remover o arquivo de teste
        File file = new File(filename);
        file.delete();
    }

    @Test
    public void testGetOverCrowded() {

        Horario  horario = new Horario("Horário 1");

        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 35, "8h", "10h", "2023-06-12");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc2", "turma2", "terça", "sala2", 20, 18, "10h", "12h", "2023-06-13");
        Bloco bloco3 = new Bloco("uc3", "turma3", "quarta", "sala3", 15, 10, "14h", "16h", "2023-06-14", "manhã");
        Bloco bloco4 = new Bloco("uc4", "turma4", "quinta", "sala4", 40, 50, "16h", "18h", "2023-06-15", "tarde");

        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);

        ArrayList<Integer> result = Utils.getOverCrowded(horario);
        ArrayList<Integer> expected = new ArrayList<Integer>();
        expected.add(0);
        expected.add(3);

        assertEquals(expected, result);
    }

    @Test
    void testGetOverCrowded_NoOvercrowded() {
        // Create a new src.test.Horario object
        Horario horario = new Horario("src.test.Horario 1");

        // Add some src.test.Bloco objects to the horario
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "8h", "10h", "2023-06-12");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc2", "turma2", "terça", "sala2", 20, 18, "10h", "12h", "2023-06-13");
        Bloco bloco3 = new Bloco("uc3", "turma3", "quarta", "sala3", 15, 10, "14h", "16h", "2023-06-14", "manhã");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);

        // Call the getOverCrowded method
        ArrayList<Integer> index = Utils.getOverCrowded(horario);

        // Assert that the index array is empty
        assertEquals(0, index.size());
    }


    @Test
    public void testGetOverlap_NoOverlap() {
        // Create blocks with no overlap
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "2023-06-12");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc2", "turma2", "terça", "sala2", 20, 18, "08:00:00", "09:30:00", "2023-06-13");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc3", "turma2", "quarta", "sala3", 20, 18, "08:00:00", "09:30:00", "2023-06-13");



        // Add blocks to the schedule
        Horario horario = new Horario();
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);

        // Ensure that no overlap is returned
        ArrayList<Integer> expected = new ArrayList<>();
        ArrayList<Integer> actual = Utils.getOverlap(horario);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetOverlap_Overlap() {
        // Create blocks with overlap
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "2023-06-12");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc2", "turma2", "segunda", "sala1", 20, 18, "08:00:00", "09:30:00", "2023-06-12");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc3", "turma2", "terça", "sala2", 20, 18, "08:00:00", "09:30:00", "2023-06-13");
        Bloco bloco4 = new Bloco("curso1", "manhã", "uc4", "turma2", "terça", "sala2", 20, 18, "08:00:00", "09:30:00", "2023-06-13");

        // Add blocks to the schedule
        Horario horario = new Horario();
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);

        // Ensure that the expected overlap is returned
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(1);
        expected.add(2);
        expected.add(3);
        ArrayList<Integer> actual = Utils.getOverlap(horario);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFirstDay(){
        int[] array = {1, 3, 5, 7, 8, 10, 12};
        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "12/06/2023");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc1", "turma1", "quarta", "sala1", 30, 20, "08:00:00", "09:30:00", "21/05/2023");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/06/2023");
        Bloco bloco4 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/05/2023");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);

        String expected1 ="27/04/2023";
        String actual1 = Utils.getFday("01/05/2023",horario,array);
        assertEquals(expected1,actual1);
        String expected2 ="19/05/2023";
        assertEquals(expected2,Utils.getFday("21/05/2023",horario,array));
        String expected3 ="28/05/2023";
        assertEquals(expected3,Utils.getFday("01/06/2023",horario,array));
        String expected4 ="12/06/2023";
        assertEquals(expected4,Utils.getFday("12/06/2023",horario,array));
    }


    @Test
    public void testGetLastDay(){
        Horario horario = new Horario();
        int[] array = {1, 3, 5, 7, 8, 10, 12};
        String date = "27/04/2022";
        String expected ="03/05/2022";
        assertEquals(expected,Utils.getLday(date,horario,array));
        String date1 = "15/04/2022";
        String expected1 ="21/04/2022";
        assertEquals(expected1,Utils.getLday(date1,horario,array));
        String date2 = "27/03/2022";
        String expected2 ="02/04/2022";
        assertEquals(expected2,Utils.getLday(date2,horario,array));
        String date3 = "27/03/2022";
        String expected3 ="02/04/2022";
        assertEquals(expected3,Utils.getLday(date3,horario,array));
    }


    @Test
    public void testCheck31(){
        int[] array = {1, 3, 5, 7, 8, 10, 12};
        assertEquals(true,check31(3,array));
        assertEquals(true,check31(12,array));
        assertEquals(true,check31(12,array));
        assertEquals(false,check31(2,array));
    }

//TODO: test getvalidDays filter


    @Test
    public void testGetValidDays(){
        int[] array = {1, 3, 5, 7, 8, 10, 12};
        ArrayList <String> expected1 = new ArrayList<String>();
        String day1 = "01/05/2023";
        String day2 = "02/05/2023";
        String day3 = "03/05/2023";
        String day4 = "04/05/2023";
        String day5 = "05/05/2023";
        String day6 = "06/05/2023";
        String day7 = "07/05/2023";
        expected1.add(day1);
        expected1.add(day2);
        expected1.add(day3);
        expected1.add(day4);
        expected1.add(day5);
        expected1.add(day6);
        expected1.add(day7);
        ArrayList<String> expected2 = new ArrayList<String>();
        day1 = "27/03/2023";
        day2 = "28/03/2023";
        day3 = "29/03/2023";
        day4 = "30/03/2023";
        day5 = "31/03/2023";
        day6 = "01/04/2023";
        day7 = "02/04/2023";
        expected2.add(day1);
        expected2.add(day2);
        expected2.add(day3);
        expected2.add(day4);
        expected2.add(day5);
        expected2.add(day6);
        expected2.add(day7);
        assertEquals(expected1,Utils.getValidDays("01/05/2023","07/05/2023",array));
        assertEquals(expected2,Utils.getValidDays("27/03/2023","02/04/2023",array));

    }
    @Test
    public void testFiltredDay(){

        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "12/06/2023");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc1", "turma1", "quarta", "sala1", 30, 20, "08:00:00", "09:30:00", "21/05/2023");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/06/2023");
        Bloco bloco4 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/05/2023");
        horario.addToHor(bloco1);
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);

        int expectedSize = 2;
        String expectedUc = "uc1";
        int expectedMaxSala = 30;
        String expectedDay = "12/06/2023";

        Horario received = Utils.filter(horario,"Dia", "12/06/2023");
        ArrayList <Bloco> blocosR = received.horario;
        int size = received.horario.size() ;
        assertEquals(expectedSize,size);
        int maxsala1 = blocosR.get(0).maxSala;
        String UC1 = blocosR.get(0).uc;
        String day1 = blocosR.get(0).dataAula;
        int maxsala2 = blocosR.get(1).maxSala;
        String UC2 = blocosR.get(1).uc;
        String day2 = blocosR.get(1).dataAula;

        assertEquals(expectedUc,UC1);
        assertEquals(expectedUc,UC2);
        assertEquals(expectedMaxSala,maxsala1);
        assertEquals(expectedMaxSala,maxsala2);
        assertEquals(expectedDay, day1);
        assertEquals(expectedDay, day2);
    }

    @Test
    public void testFiltredWeek() {

        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "12/06/2023");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc1", "turma1", "quarta", "sala1", 30, 20, "08:00:00", "09:30:00", "21/05/2023");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/06/2023");
        Bloco bloco4 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/05/2023");
        Bloco bloco5 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "27/04/2023");
        Bloco bloco6 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "03/05/2023");
        Bloco bloco7 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "04/05/2023");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);
        horario.addToHor(bloco5);
        horario.addToHor(bloco6);
        horario.addToHor(bloco7);

        int expectedSize = 3;
        String expectedUC = "uc1";
        String expectedDate1 = "01/05/2023";
        String expectedDate2 = "27/04/2023";
        String expectedDate3 = "03/05/2023";

        Horario received = Utils.filter(horario,"Semana", "01/05/2023");
        ArrayList <Bloco> blocosR = received.horario;
        int size = received.horario.size() ;
        String UC1 = blocosR.get(0).uc;
        String UC2 = blocosR.get(1).uc;
        String UC3 = blocosR.get(2).uc;
        String day1 = blocosR.get(0).dataAula;
        String day2 = blocosR.get(1).dataAula;
        String day3 = blocosR.get(2).dataAula;


        assertEquals(expectedSize,size);
        assertEquals(expectedUC,UC1);
        assertEquals(expectedUC,UC2);
        assertEquals(expectedUC,UC3);
        assertEquals(expectedDate1, day1);
        assertEquals(expectedDate2, day2);
        assertEquals(expectedDate3, day3);


    }


    @Test
    public void testFilerMonth(){
        Horario horario = new Horario();
        Bloco bloco1 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "12/06/2023");
        Bloco bloco2 = new Bloco("curso1", "manhã", "uc1", "turma1", "quarta", "sala1", 30, 20, "08:00:00", "09:30:00", "21/05/2023");
        Bloco bloco3 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/06/2023");
        Bloco bloco4 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "01/05/2023");
        Bloco bloco5 = new Bloco("curso1", "manhã", "uc1", "turma1", "segunda", "sala1", 30, 20, "08:00:00", "09:30:00", "27/04/2023");
        Bloco bloco6 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "03/05/2023");
        Bloco bloco7 = new Bloco("curso1", "manhã", "uc1", "turma1", "sexta", "sala1", 30, 20, "08:00:00", "09:30:00", "04/05/2023");
        horario.addToHor(bloco1);
        horario.addToHor(bloco2);
        horario.addToHor(bloco3);
        horario.addToHor(bloco4);
        horario.addToHor(bloco5);
        horario.addToHor(bloco6);
        horario.addToHor(bloco7);

        int expectedSize = 4;
        String expectedUC = "uc1";
        String expectedDate1 = "21/05/2023";
        String expectedDate2 = "01/05/2023";
        String expectedDate3 = "03/05/2023";
        String expectedDate4 = "04/05/2023";

        Horario received = Utils.filter(horario,"Mes", "01/05/2023");
        ArrayList <Bloco> blocosR = received.horario;
        int size = received.horario.size() ;
        String UC1 = blocosR.get(0).uc;
        String UC2 = blocosR.get(1).uc;
        String UC3 = blocosR.get(2).uc;
        String UC4 = blocosR.get(3).uc;
        String day1 = blocosR.get(0).dataAula;
        String day2 = blocosR.get(1).dataAula;
        String day3 = blocosR.get(2).dataAula;
        String day4 = blocosR.get(3).dataAula;
        assertEquals(expectedSize,size);
        assertEquals(expectedUC,UC1);
        assertEquals(expectedUC,UC2);
        assertEquals(expectedUC,UC3);
        assertEquals(expectedUC,UC4);
        assertEquals(expectedDate1, day1);
        assertEquals(expectedDate2, day2);
        assertEquals(expectedDate3, day3);
        assertEquals(expectedDate4, day4);
    }

    @Test
    void testFromWebcalToHorario() {
        // Testa se o método retorna um horário não nulo
        Horario horario = Utils.fromWebcalToHorario("webcal://fenix.iscte-iul.pt/publico/publicPersonICalendar.do?method=iCalendar&username=pdrao@iscte.pt&password=jnyDmcLOe9Jw8LJ66AIQEnKbINOGJog2wdMWImltULxCPVdXeYLjTKlxzvNtxV8tcDN95j4cEF7N0JhquotwLtCgZPCuM5WLYYmtwHFzIjT5h2kfO38fZQOGH7MAnoSq");
        assertNotNull(horario);

        // Testa se o método retorna um horário vazio para uma URI inválida
        Horario horarioInvalido = Utils.fromWebcalToHorario("https://exemplo.com/nao_existe.ics");
        assertTrue(horarioInvalido.isEmpty());

        //Testa se o método retorna um horário com pelo menos um bloco para uma URI válida
        assertFalse(horario.isEmpty());

        // Testa se o método retorna um horário com os atributos do bloco corretos para uma URI válida
        System.out.println(horario.getHorario().get(0));
        Bloco bloco = horario.getHorario().get(0);
        assertEquals(0, bloco.getMaxSala());
        assertEquals(0, bloco.getnInscritos());
        assertEquals("18:00:00", bloco.getHoraInicioUC());
        assertEquals("19:30:00", bloco.getHoraFimUC());
        assertEquals("13/09/2022", bloco.getDataAula());


    }

}



