package src;


/**
 * Objecto Bloco
 * objecto responsavel por armazenar as informaçoes de cada bloco de aulas
 */




public class Bloco {

    public  String uc,turma,dia_sem,sala, curso, turno,horaInicioUC,horaFimUC, dataAula;
    public int maxSala,nInscritos;


    public Bloco(String curso,String turno,String uc, String turma, String dia_sem, String sala, int maxSala, int nInscritos, String horaInicioUC, String horaFimUC, String dataAula) {

        this.uc = uc;
        this.turma = turma;
        this.curso = curso;
        this.dia_sem = dia_sem;
        this.sala = sala;
        this.maxSala = maxSala;
        this.nInscritos = nInscritos;
        this.horaInicioUC = horaInicioUC;
        this.horaFimUC = horaFimUC;
        this.dataAula = dataAula;
        this.turno = turno;
    }
    public Bloco(String uc, String turma, String dia_sem, String sala, int maxSala, int nInscritos, String horaInicioUC, String horaFimUC, String dataAula, String turno) {
        this.uc = uc;
        this.curso = "unidadeIsolada";
        this.turma = turma;
        this.dia_sem = dia_sem;
        this.sala = sala;
        this.maxSala = maxSala;
        this.nInscritos = nInscritos;
        this.horaInicioUC = horaInicioUC;
        this.horaFimUC = horaFimUC;
        this.dataAula = dataAula;
        this.turno = turno;
    }





    public Bloco() {
    }

    public String getUc() {
        return uc;
    }

    public String getTurno() {return  turno;}

    public String getCurso() {return curso;}
    public void setUc(String uc) {
        this.uc = uc;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getDia_sem() {
        return dia_sem;
    }

    public void setDia_sem(String dia_sem) {
        this.dia_sem = dia_sem;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public int getMaxSala() {
        return maxSala;
    }

    public void setMaxSala(int maxSala) {
        this.maxSala = maxSala;
    }

    public int getnInscritos() {
        return nInscritos;
    }

    public void setnInscritos(int nInscritos) {
        this.nInscritos = nInscritos;
    }




    public String getHoraInicioUC() {

        return horaInicioUC;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public void setHoraInicioUC(String horaInicioUC) {
        this.horaInicioUC = horaInicioUC;
    }

    public String getHoraFimUC() {
        return horaFimUC;
    }

    public void setHoraFimUC(String horaFimUC) {
        this.horaFimUC = horaFimUC;
    }

    public String getDataAula() {
        return dataAula;
    }

    public void setDataAula(String dataAula) {
        this.dataAula = dataAula;
    }
}

