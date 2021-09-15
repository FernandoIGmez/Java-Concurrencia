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
public class PiscinaOlas {

    private ColaThreads entradaPiscina;
    private ColaThreads zonaPiscina;
    private Semaphore semaforoPiscina = new Semaphore(20, true);
    private Semaphore semaforoUsuariosIndividuales = new Semaphore(2, true);//SEMAFORO QUE REALIZA UNA SC EN LA QUE SOLO HABRA DOS 
    //USUARIOS, LOS CUALES SON NECESARIOS PARA ENTRAR A LA PISCINA
    private Parque p;
    private ReentrantLock cerrojoPiscina = new ReentrantLock();
    private Condition despertarUsuarios = cerrojoPiscina.newCondition();
    private Condition despertarMonitor = cerrojoPiscina.newCondition();
    private Condition despertarUsuariosIndividuales = cerrojoPiscina.newCondition();

    private ReentrantLock cerrojoAcompañante = new ReentrantLock();
    private Condition condicionAcompañante = cerrojoAcompañante.newCondition();

    private ReentrantLock cerrojoUsuariosIndividuales = new ReentrantLock();
    private Condition condicionParejas = cerrojoUsuariosIndividuales.newCondition();

    private AtomicBoolean usuariosColaMirados = new AtomicBoolean(true); //Cuando todos los ususarios de la cola hayan sido vistos por el monitor se pone a true

    public PiscinaOlas(Parque p, JTextField tfCola, JSpinner jsCola, JTextField tfZona, JSpinner jsZona) {
        this.p = p;
        entradaPiscina = new ColaThreads(tfCola, jsCola);
        zonaPiscina = new ColaThreads(tfZona, jsZona);
    }

    public void entraPiscinaOLAS(Usuario u) {
        int tiempoActividad = (int) (Math.random() * 3) + 2;
        tiempoActividad *= 1000;
        try {

            entradaPiscina.meter(u);
            if (u.isAcompañado()) {
                u.getUsuarioAsociado().setTiempoActividad(tiempoActividad);
                entradaPiscina.meter(u.getUsuarioAsociado());//p
                
            }
            p.estaParqueCerrado();
            usuariosColaMirados.compareAndExchange(true, false);
            cerrojoPiscina.lock();
            despertarMonitor.signal();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuarios.await();
            }
            despertarMonitor.signal();//
        } catch (InterruptedException e) {
        } finally {
            cerrojoPiscina.unlock();
        }

        try {

            if (u.isAcompañado()) { //SI ESTA ACOMPAÑADO

                if (u.isPuedePasar()) { //SI PUEDEN PASAR
                    //   u.getUsuarioAsociado().setPuedePasar(true);//p
                    //   u.getUsuarioAsociado().setTiempoActividad(tiempoActividad);//p
                    semaforoPiscina.acquire(2);
                    entradaPiscina.sacar(u);
                    entradaPiscina.sacar(u.getUsuarioAsociado());
                    zonaPiscina.meter(u);
                    zonaPiscina.meter(u.getUsuarioAsociado());
                    p.estaParqueCerrado();
                    try {
                        Thread.sleep(tiempoActividad);
                        p.haRealizadoActividad(u);
                    } catch (InterruptedException ex) {
                    }
                    p.estaParqueCerrado();
                    zonaPiscina.sacar(u);
                    zonaPiscina.sacar(u.getUsuarioAsociado());
                    semaforoPiscina.release(2); //AQUI ACABAN LOS USUARIOS QUE VAN ACOMPAÑADOS Y PUEDEN ENTRAR
                } else {                        //SI SON ACOMPAÑADOS PERO NO PUEDEN PASAR
                    p.estaParqueCerrado();
//                    u.getUsuarioAsociado().setPuedePasar(false);
                    entradaPiscina.sacar(u);
                    entradaPiscina.sacar(u.getUsuarioAsociado());
                    try {
                        cerrojoAcompañante.lock();
                        condicionAcompañante.signalAll();

                    } catch (Exception x) {
                    } finally {
                        cerrojoAcompañante.unlock();
                    }
                }                               //AQUI ACABAN LOS USUARIOS QUE VAN ACOMPAÑADOS Y NO PUEDEN ENTRAR

            } else { //SI NO ESTA ACOMPAÑADO PODRA PASAR PERO TIENE QUE ESPERAR A QUE HAYA OTRA PERSONA PREPARADA PARA PASAR
                try {
                    semaforoUsuariosIndividuales.acquire();
                    cerrojoUsuariosIndividuales.lock();
                    while (semaforoUsuariosIndividuales.availablePermits() > 0) {//CUANDO HAYA MAS DE 0 PERMISOS SIGNIFICA QUE HAY
                        condicionParejas.await();                          //0 O 1 USUARIO ESPERANDO A SER EMPAREJADO
                    }
                    condicionParejas.signal();
                } catch (InterruptedException e) {
                } finally {
                    cerrojoUsuariosIndividuales.unlock();
                }
                try {
                    semaforoPiscina.acquire();
                    semaforoUsuariosIndividuales.release();
                    p.estaParqueCerrado();
                    entradaPiscina.sacar(u);
                    zonaPiscina.meter(u);
                    p.estaParqueCerrado();
                    
                    
                    Thread.sleep(tiempoActividad);
                    p.haRealizadoActividad(u);
                    
                    p.estaParqueCerrado();
                    zonaPiscina.sacar(u);
                    semaforoPiscina.release();
                } catch (InterruptedException e) {
                }

            }//FIN ACCION USUARIOS INDIVIDUALES

        } catch (InterruptedException s) {
        }
        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoPiscinaOLAS(UsuarioAcompañante u) {
        try {
            cerrojoAcompañante.lock();
            while (!u.isMonitorHaAcabado()) {
                condicionAcompañante.await();
            }

        } catch (InterruptedException e) {
        } finally {
            cerrojoAcompañante.unlock();
        }
        p.estaParqueCerrado();
        try {
            if (u.isPuedePasar()) {
                Thread.sleep(u.getTiempoActividad());
                p.haRealizadoActividad(u);
            }
            p.estaParqueCerrado();
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
            if (entradaPiscina.contiene(u)) {
                entradaPiscina.sacar(u);
            }

        } catch (InterruptedException s) {
        }
    }

    public void monitorPiscina() {

        try {
            p.estaParqueCerrado();
            cerrojoPiscina.lock();

            while (entradaPiscina.size() <= 0 || usuariosColaMirados.get()) {
                // despertarUsuarios.signalAll();//p
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

                        Thread.sleep(2000);
                        u.setMonitorHaAcabado(true);
                        if (u.getEdad() < 18) {//PRUEBA MONITOR DESPIERTA AMBOS
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            
                            if (u.getEdad() < 6 || u.getUsuarioAsociado().getEdad() < 6) {
                                u.setPuedePasar(false);
                                u.getUsuarioAsociado().setPuedePasar(false);

                            } else {
                                u.setPuedePasar(true);
                                u.getUsuarioAsociado().setPuedePasar(true);
                            }
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
                if (entradaPiscina.monitorHaMiradoTodos()) {
                    usuariosColaMirados.compareAndExchange(false, true);
                    i = 400;
                }
            }
            cerrojoPiscina.lock();
            cerrojoAcompañante.lock();///
           
            despertarUsuarios.signalAll();
            condicionAcompañante.signalAll();
        } catch (InterruptedException ex) {
        } finally {
            cerrojoAcompañante.unlock();
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

}
