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

import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author FERNANDO
 */
public class Vestuario {

    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
     */
    /**
     *
     * @author FERNANDO
     */
    
    private ColaThreads entradaVestuario;
    private ColaThreads zonaVestuario;
    private Semaphore semaforoAdultos = new Semaphore(20, true);
    private Semaphore semaforoNiños = new Semaphore(10, true);
    private Parque p;
    private ReentrantLock cerrojoVestuario = new ReentrantLock();
    
    private ReentrantLock cerrojoAcompañante = new ReentrantLock();
    private Condition condicionAcompañante= cerrojoAcompañante.newCondition();
    
    private Condition despertarUsuarios = cerrojoVestuario.newCondition();
    private Condition despertarMonitor = cerrojoVestuario.newCondition();
    private AtomicBoolean usuariosColaMirados = new AtomicBoolean(true); //Cuando todos los ususarios de la cola hayan sido vistos por el monitor se pone a true

    public Vestuario(Parque p, JTextField tfColaV, JSpinner jsColaV, JTextField tfZonaV, JSpinner jsZonaV) {
        this.p = p;
        entradaVestuario = new ColaThreads(tfColaV, jsColaV);
        zonaVestuario = new ColaThreads(tfZonaV, jsZonaV);
    }

    public void entraVestuario(Usuario u) {

        try {          
            entradaVestuario.meter(u);
            
            if (u.isAcompañado()) {
                entradaVestuario.meter(u.getUsuarioAsociado());
            }
            //
            p.estaParqueCerrado();
            //
            usuariosColaMirados.compareAndExchange(true, false);
            cerrojoVestuario.lock();
            despertarMonitor.signal();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuarios.await();
            }
            
        } catch (InterruptedException e) {
        } finally {
            cerrojoVestuario.unlock();
        }

        try {

            if (u.isAcompañado()) {
                semaforoNiños.acquire(2);
                //
                p.estaParqueCerrado();
                //
                entradaVestuario.sacar(u);
                entradaVestuario.sacar(u.getUsuarioAsociado());
                zonaVestuario.meter(u);
                zonaVestuario.meter(u.getUsuarioAsociado());
                /////////////////////////////
                try{
                    cerrojoAcompañante.lock();
                condicionAcompañante.signalAll();
                }catch(Exception e){}
                finally{
                    cerrojoAcompañante.unlock();
                }
                /////////////////////////////
                //
                p.estaParqueCerrado();
                //
            } else if (!u.isAcompañado() && u.getEdad() > 10 && u.getEdad() < 18) {
                semaforoNiños.acquire();
                //
                p.estaParqueCerrado();
                //
                entradaVestuario.sacar(u);
                zonaVestuario.meter(u);
                //
                p.estaParqueCerrado();
                //
            } else if (!u.isAcompañado() && u.isAdulto()) {
                semaforoAdultos.acquire();
                //
                p.estaParqueCerrado();
                //
                entradaVestuario.sacar(u);
                zonaVestuario.meter(u);
                //
                p.estaParqueCerrado();
                //
            }
               try{
                    cerrojoVestuario.lock();
                    despertarMonitor.signalAll();
                }catch (Exception e){}
                finally{
                    cerrojoVestuario.unlock();
                }
            
            Thread.sleep(3000);
            //
            p.estaParqueCerrado();
            //
            zonaVestuario.sacar(u);

            if (u.isAcompañado()) {
                zonaVestuario.sacar(u.getUsuarioAsociado());
                semaforoNiños.release(2);
            } else {
                if(u.getEdad() > 10 && u.getEdad() < 18) {
                semaforoNiños.release();}
                 else  {
                semaforoAdultos.release();
            }}
      
        } catch (InterruptedException e) {
               System.out.println("Excepcion Sacar vest");
        } finally {

        }

    u.setMonitorHaAcabado(false);
    u.setPuedePasar(false);
    }

    public void usuarioAsociadoVestuarioV2(UsuarioAcompañante u){
       try{ cerrojoAcompañante.lock();
        while(!u.isMonitorHaAcabado()){
            condicionAcompañante.await();
        }
       }catch(InterruptedException e){}
       finally{
           cerrojoAcompañante.unlock();
       }
        try {
            //
            p.estaParqueCerrado();
            //
            Thread.sleep(3000);
            //
            p.estaParqueCerrado();
            //
        } catch (InterruptedException ex) {}
        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
        
    }
    
    public void monitorVestuario() {
        p.estaParqueCerrado();
        try {
            cerrojoVestuario.lock();

            while (entradaVestuario.size() <= 0 || usuariosColaMirados.get()) {
                despertarMonitor.await();

            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoVestuario.unlock();
        }

        try {

            for (int i = 0; i < entradaVestuario.size(); i++) {
                Usuario u = entradaVestuario.getPosicion(i);
                if (u.isAcompañado() ) {
                    if (!u.isMonitorHaAcabado()&& i < 400) {
                        Thread.sleep(2000);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(true);
                        if(u.getEdad()<18){//PRUEBA MONITOR DESPIERTA AMBOS
                        u.getUsuarioAsociado().setMonitorHaAcabado(true);   
                        u.getUsuarioAsociado().setPuedePasar(true);
                        }
                       
                        i = 400;
                    }
                    

                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        Thread.sleep(1000);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(true);
                        i = 400;
                    }
                }

                if (entradaVestuario.monitorHaMiradoTodos()) {
                    usuariosColaMirados.compareAndExchange(false, true);
                    i = 400;
                }
            }
            cerrojoVestuario.lock();
            despertarUsuarios.signalAll();

        } catch (InterruptedException ex) {
        } finally {
            cerrojoVestuario.unlock();
        }
    }

    public void meterColaVestuario(Usuario u) {
        entradaVestuario.meter(u);
    }

    public void meterZonaVestuario(Usuario u) {
        zonaVestuario.meter(u);
    }

    public ColaThreads getEntradaVestuario() {
        return entradaVestuario;
    }

    public ColaThreads getZonaVestuario() {
        return zonaVestuario;
    }

    public void verPuedenPasar() {
        String contenido = "";
        for (int i = 0; i < entradaVestuario.size(); i++) {
            contenido += ">" + entradaVestuario.getPosicion(i).getIDentificacion() + "-";
            contenido += entradaVestuario.getPosicion(i).isMonitorHaAcabado() + "< ";
        }
        System.out.println(contenido);
        System.out.println(usuariosColaMirados.get());
    }

}
