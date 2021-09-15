/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author FERNANDO
 */
public class PiscinaNiños {

    private ColaThreads entradaPiscina;
    private ColaThreads zonaPiscina;
    private ColaThreads zonaEsperaAcompañantes;

    private Semaphore semaforoPiscina = new Semaphore(15, true);
    private Parque p;

    private ReentrantLock cerrojoPiscina = new ReentrantLock();
    private Condition despertarUsuarios = cerrojoPiscina.newCondition();
    private Condition despertarMonitor = cerrojoPiscina.newCondition();

    private ReentrantLock cerrojoAcompañante = new ReentrantLock();
    private Condition condicionAcompañante = cerrojoAcompañante.newCondition();

    private AtomicBoolean usuariosColaMirados = new AtomicBoolean(true); //Cuando todos los ususarios de la cola hayan sido vistos por el monitor se pone a true

    public PiscinaNiños(Parque p, JTextField tfCola, JSpinner jsCola, JTextField tfZona, JSpinner jsZona, JTextField tfZonaAcomp, JSpinner jsZonaAcomp) {
        this.p = p;
        entradaPiscina = new ColaThreads(tfCola, jsCola);
        zonaPiscina = new ColaThreads(tfZona, jsZona);
        zonaEsperaAcompañantes = new ColaThreads(tfZonaAcomp, jsZonaAcomp);
    }

    public void entraPiscina(Usuario u) {
        int tiempoActividad = (int) (Math.random() * 2) + 1;
        tiempoActividad *= 1000;
        try {
            entradaPiscina.meter(u);
            if (u.isAcompañado()) {
                entradaPiscina.meter(u.getUsuarioAsociado());
            }
            p.estaParqueCerrado();
            usuariosColaMirados.compareAndExchange(true, false);
            cerrojoPiscina.lock();
            despertarMonitor.signal();
            while (!u.isMonitorHaAcabado()) {

                despertarUsuarios.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoPiscina.unlock();
        }

        try {
            if (u.isPuedePasar()) {
                if (u.getEdad() <= 5) {    //SI PUEDEN PASAR LOS DOS A LA PISCINA ENTRAN
                    semaforoPiscina.acquire(2);
         
                    entradaPiscina.sacar(u);
                    entradaPiscina.sacar(u.getUsuarioAsociado());
                    zonaPiscina.meter(u);
                    zonaPiscina.meter(u.getUsuarioAsociado());
                    p.estaParqueCerrado();
                } else {   //SI SOLO PUEDE PASAR EL NIÑO, EL ACOMPAÑANTE LE ESPERARA HASTA QUE ACABE
                    semaforoPiscina.acquire();
                    entradaPiscina.sacar(u);
                    entradaPiscina.sacar(u.getUsuarioAsociado());
                    zonaPiscina.meter(u);
                    zonaEsperaAcompañantes.meter(u.getUsuarioAsociado());
                    p.estaParqueCerrado();
                }
                try{
                Thread.sleep(tiempoActividad);}
                catch(InterruptedException e){}
                p.estaParqueCerrado();
                zonaPiscina.sacar(u);
                //SI EL USUARIO ACOMPAANTE HA ENTRADO EN LA PISCINA SE SACA A EL TAMBIEN
                if (u.getEdad() <= 5) {
                    
                    zonaPiscina.sacar(u.getUsuarioAsociado());
                    semaforoPiscina.release(2);
                } else {   //SI NO HA ENTRADO SE LE DESPIERTA MEDIANTE EL CONDITION DEL ACOMPAÑANTE
                    u.getUsuarioAsociado().setNiñoHaAcabadoPiscina(true);
                    zonaEsperaAcompañantes.sacar(u.getUsuarioAsociado());
                    semaforoPiscina.release();
                    try{
                        cerrojoAcompañante.lock();
                        condicionAcompañante.signalAll();
                    }catch(Exception e){}
                    finally{
                        cerrojoAcompañante.unlock();
                    }
                }
                p.haRealizadoActividad(u);
            } else {
                p.estaParqueCerrado();
                entradaPiscina.sacar(u);
                entradaPiscina.sacar(u.getUsuarioAsociado());
            }

        } catch (InterruptedException e) {
        } finally {

        }

        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoPiscina(UsuarioAcompañante u) {
        try {
            cerrojoPiscina.lock();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuarios.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoPiscina.unlock();
        }
        p.estaParqueCerrado();
        if (u.isPuedePasar()) { //SI EL ACOMPAÑANTE PASA DUERME LA MISMA CANTIDAD DE TIEMPO QUE EL USUARIO AL QUE ACOMPAÑA
            try {
                Thread.sleep(u.getTiempoActividad());
                
            } catch (InterruptedException ex) {
            }
            p.estaParqueCerrado();
        } else {
            try {
                cerrojoAcompañante.lock();
                while (!u.isNiñoHaAcabadoPiscina()) {
                    condicionAcompañante.await();
                }
                u.setNiñoHaAcabadoPiscina(false);
                zonaEsperaAcompañantes.sacar(u);
            } catch (InterruptedException x) {
            } finally {
                cerrojoAcompañante.unlock();
            }
            zonaPiscina.sacar(u.getUsuarioAcompañado());
            p.estaParqueCerrado();
        }
        p.haRealizadoActividad(u);
        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void monitorPiscina() {
         p.estaParqueCerrado();

        try {
            cerrojoPiscina.lock();

            while (entradaPiscina.size() <= 0 || usuariosColaMirados.get()) {
                despertarUsuarios.signalAll();
                despertarMonitor.await();

            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoPiscina.unlock();
        }

        try {

            for (int i = 0; i < entradaPiscina.size(); i++) {
                Usuario u = entradaPiscina.getPosicion(i);
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        int tiempoaleat=(int) (Math.random()*5)+10;
                        Thread.sleep(tiempoaleat*100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(true);
                        if (u.getEdad() < 18) {//PRUEBA MONITOR DESPIERTA AMBOS
                            
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            if (u.getEdad()<6){ //DE 1 A 5 AÑOS CON ACOMPAÑANTE EN LA PISCINA
                                u.getUsuarioAsociado().setPuedePasar(true);
                            }
                            else{               //DE 6 A 10 SIN ACOMPAÑANTE
                                u.getUsuarioAsociado().setPuedePasar(false);
                            }
                            
                        }
                        
                        i = 400;
                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        int tiempoaleat=(int) (Math.random()*5)+10;
                        Thread.sleep(tiempoaleat*100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(false);

                        i = 400;
                    }
                }
                if (entradaPiscina.monitorHaMiradoTodos()) {
                    usuariosColaMirados.compareAndExchange(false, true);
                    i = 400;
                }
            }
            cerrojoPiscina.lock();
    
            despertarUsuarios.signalAll();
            
        } catch (InterruptedException ex) {
        } finally {
        
            cerrojoPiscina.unlock();
        }
    }

    public void meterColaPiscina(Usuario u) {
        entradaPiscina.meter(u);
    }

    public void meterZonaPiscina(Usuario u) {
        zonaPiscina.meter(u);
    }

    public ColaThreads getEntradaPiscina() {
        return entradaPiscina;
    }

    public ColaThreads getZonaPiscina() {
        return zonaPiscina;
    }

    public ColaThreads getZonaEsperaPiscina() {
        return zonaEsperaAcompañantes;
    }

    public void verPuedenPasar() {
        String contenido = "";
        for (int i = 0; i < entradaPiscina.size(); i++) {
            contenido += ">" + entradaPiscina.getPosicion(i).getIDentificacion() + "-";
            contenido += entradaPiscina.getPosicion(i).isMonitorHaAcabado() + "< ";
        }
        System.out.println(contenido);
        System.out.println(usuariosColaMirados.get());
    }

}
