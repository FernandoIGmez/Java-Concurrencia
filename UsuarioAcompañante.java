/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
/**
 *
 * @author FERNANDO
 */
public class UsuarioAcompañante extends Usuario{
    
    private boolean pasaParque=false;
    private boolean heAcabadoActividad=false;
    private boolean niñoHaAcabadoPiscina = false;
    private Usuario usuarioAcompañado;
    private int idUsuarioAcompañado,tiempoActividad=0;
    
    
    public UsuarioAcompañante(int id, Parque parque,Usuario acompañado) {
        super(id, parque);
        setIdAcompañante(id-1);
        setEdad( (int) (Math.random()*32)+18);     
        setAdulto(true);
        setAcompañado(true);
        this.usuarioAcompañado=acompañado;
       
        
    }

    
    public void run(){
      
       parque.elegirsitioAcompañante(this);
       parque.getVestuario().usuarioAsociadoVestuarioV2(this);
        while (super.getActividadesRealizadas()<super.getActividadesARealizar()){  
            
            try {

                parque.elegirsitioAcompañante(this);

                if (actividadActual == 1) {
                    parque.getPiscinaNiños().usuarioAsociadoPiscina(this);
                    
                } else if (actividadActual == 2) {
                    parque.getPiscinaOlas().usuarioAsociadoPiscinaOLAS(this);
                   
                } else if (actividadActual == 3) {
                    parque.getPiscinaGrande().usuarioAsociadoEntraPiscina(this);
                   
                } else if (actividadActual == 4) {
                    parque.getTumbonas().usuarioAsociadoTumbonas(this);
                    
                } else if (actividadActual == 5) {
                    parque.getPiscinaGrande().usuarioAsociadoEntraToboganA(this);
                    
                 } else if (actividadActual == 6) {
                     parque.getPiscinaGrande().usuarioAsociadoEntraToboganB(this);
                     
                }  else if (actividadActual == 7) {
                    parque.getPiscinaGrande().usuarioAsociadoEntraToboganC(this);
                }
            } catch (Exception e) {
                System.out.println("EXCEPCION EN run() ACOMPAÑANTE");
            }
        }
      parque.getVestuario().usuarioAsociadoVestuarioV2(this);
  
 
    }

    public void setPasaParque(boolean pasaParque) {
        this.pasaParque = pasaParque;
    }

    

    public boolean isPasaParque() {
        return pasaParque;
    }

   

    public int getTiempoActividad() {
        return tiempoActividad;
    }

    public void setTiempoActividad(int tiempoActividad) {
        this.tiempoActividad = tiempoActividad;
    }

    public boolean isHeAcabadoActividad() {
        return heAcabadoActividad;
    }

    public void setHeAcabadoActividad(boolean heAcabadoActividad) {
        this.heAcabadoActividad = heAcabadoActividad;
    }

  

    public Usuario getUsuarioAcompañado() {
        return usuarioAcompañado;
    }

    public void setUsuarioAcompañado(Usuario usuarioAcompañado) {
        this.usuarioAcompañado = usuarioAcompañado;
    }

    public boolean isNiñoHaAcabadoPiscina() {
        return niñoHaAcabadoPiscina;
    }

    public void setNiñoHaAcabadoPiscina(boolean niñoHaAcabadoPiscina) {
        this.niñoHaAcabadoPiscina = niñoHaAcabadoPiscina;
    }

    
    
}
