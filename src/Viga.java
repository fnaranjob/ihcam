/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FNaranjoB

public class Viga {
    
    private double AnchoViga = 100, AltoViga = 100, RadioAla=10, RadioAlma=10, EspesorAla=5, EspesorAlma=5;

    public Viga() {
        AnchoViga = 100;
        AltoViga = 100;
        RadioAla = 10;
        RadioAlma = 10;
        EspesorAla = 5;
        EspesorAlma = 5;
    }
   
    public void SetearViga(double AnchoViga, double AltoViga, double RadioAla, double RadioAlma, double EspesorAla, double EspesorAlma){
        
        this.AnchoViga = AnchoViga;
        this.AltoViga = AltoViga;
        this.RadioAla = RadioAla;
        this.RadioAlma = RadioAlma;
        this.EspesorAla = EspesorAla;
        this.EspesorAlma = EspesorAlma;
        
    }

    public double GetAncho(){return AnchoViga;}
    public double GetAlto(){return AltoViga;}
    public double GetRadioAla(){return RadioAla;}
    public double GetRadioAlma(){return RadioAlma;}
    public double GetEspesorAla(){return EspesorAla;}
    public double GetEspesorAlma(){return EspesorAlma;}
    
    public double GetEscalaLateralDisplay(int anchod,int altod){
        
    double e,h;
        
        e=7.0/6.0;
        h=((double)AltoViga)*e;
        if(h>((double)altod))
            e=((double)altod)/((double)AltoViga);
        return e;
    
    }
    
    public double GetEscalaSuperiorDisplay(int anchod,int altod){
        
        double e,h;
        e=7.0/6.0;
        h=(double)AnchoViga*e;
        if(h>((double)altod))
            e=((double)altod)/((double)AnchoViga);
        return e;
    
    }
    
}
