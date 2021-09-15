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
public class CreaUsuarios extends Thread{
    private int numUsuariosCrear;
   private Parque p;
    public CreaUsuarios(Parque p, int numUsuariosCrear){
        this.p=p;
        this.numUsuariosCrear=numUsuariosCrear;
    }
    public void run(){
        for (int i = 1; i <= numUsuariosCrear; i++) {
            
            Usuario u = new Usuario(i, p);
            int actividadesArealizar= (int) (Math.random()*10)+5;   
            u.setActividadesARealizar(actividadesArealizar);
            if (u.isAcompañado()) {
                i++;
                UsuarioAcompañante acompañante = new UsuarioAcompañante(i, p, u);  //creacion del acompañante con el segundo constructor
                acompañante.setActividadesARealizar(actividadesArealizar);
                
                acompañante.setIdAcompañante(u.getIdUsuario());
                u.setUsuarioAcompañante(acompañante);
                u.setIdAcompañante(acompañante.getIdUsuario());               
                u.start();
                acompañante.start();
            }
            else u.start();
            try {
                
                int dormirCreadorUsuarios = (int) (Math.random()*500)+300;
                sleep(dormirCreadorUsuarios);
                
            } catch (InterruptedException ex) {
            }
        } 
        
    }
}

