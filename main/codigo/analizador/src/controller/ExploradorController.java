package controller;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import model.LineaPedido;


public class ExploradorController {
    //listas de pedidos
    Scanner keyScanner=new Scanner(System.in);
    private List<LineaPedido> Pedidos;
    //una lista para almacenar los pedidos filtrados, se mantiene como atributo de la clase para que los métodos de filtrado puedan acceder a ella y modificarla según los criterios de filtrado seleccionados por el usuario
    private static  ExploradorController instance=null;
    public static ExploradorController getInstance(){
        if(instance==null){
            instance=new ExploradorController();
        }
        return instance;
    }
    //constructor privado para evitar instanciación directa y asegurar el uso del singleton
    private   ExploradorController() {
      Pedidos=new ArrayList<>();
    }
    // un formato de fecha para validar las fechas de los pedidos, se utiliza el mismo formato que se espera en los datos de entrada (dd-mm-aaaa)
    private  static final DateTimeFormatter DATE_TIME_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    public DateTimeFormatter  getdate(){
      return  DATE_TIME_FORMATTER;
    }
    //metodo para validar y añadir pedidos a la lista de pedidos
    public  void setPedidos(List<LineaPedido> p) {
      if(p.isEmpty()){
        System.out.println("No hay pedidos para mostrar.");
      } 
      for(int i=0;i<p.size();i++){
        boolean valido=true;
        //1.unidad
        //comprobar 1-10
        if(p.get(i).getUnidades()<0){
          System.out.printf("El pedido con id %d tiene unidades negativas, lo que no es válido.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //2.coste unitario
        if(p.get(i).getCosteUnitario()<0){
          System.out.printf("El pedido con id %d tiene coste unitario negativo, lo que no es válido.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //3.precio venta unitario
        if(p.get(i).getPrecioVentaUnitario()<0){
          System.out.printf("El pedido con id %d tiene precio de venta unitario negativo, lo que no es válido.\n", p.get(i).getIdPedido());
            valido=false;
        } 
        //4.fecha pedido
        if(p.get(i).getFechaPedido()==null || p.get(i).getFechaPedido().isEmpty()){
          System.out.printf("El pedido con id %d no tiene fecha de pedido válida.\n", p.get(i).getIdPedido());
          valido=false;
        }else{
         try{
          //validar formato de fecha utilizando el DateTimeFormatter definido anteriormente, si la fecha no cumple con el formato esperado se lanzará una excepción que se captura para marcar el pedido como no válido
            DATE_TIME_FORMATTER.parse(p.get(i).getFechaPedido());
          }catch(Exception e){
            System.out.printf("El pedido con id %d tiene una fecha de pedido con formato incorrecto, lo que no es válido.\n", p.get(i).getIdPedido());
            valido=false;
          }
        }
        //5.categoria
        if(p.get(i).getCategoria()==null || p.get(i).getCategoria().isEmpty()){
          System.out.printf("El pedido con id %d no tiene categoría válida.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //6.zona comercial
        if(p.get(i).getZonaComercial()<0){
          System.out.printf("El pedido con id %d no tiene zona comercial válida.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //7.estado pedido
        if(p.get(i).getEstado()==null){
          System.out.printf("El pedido con id %d no tiene estado válido.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //8.referencia producto
        if(p.get(i).getReferenciaProduto()==null || p.get(i).getReferenciaProduto().isEmpty()){
          System.out.printf("El pedido con id %d no tiene referencia de producto válida.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //9.idpedido
        if(p.get(i).getIdPedido()<=0){
          System.out.printf("El pedido con id %d tiene un ID no válido.\n", p.get(i).getIdPedido());
          valido=false;
        }
        //10.id linea
        if(p.get(i).getIdLinea()<=0){
          System.out.printf("El pedido con id %d tiene un ID linea no válido.\n", p.get(i).getIdLinea());
          valido=false;
        }
        //11.anadir pedido a la lista de pedidos si es valido
        if(valido){
          Pedidos.add(p.get(i));
        }
      }
}
public List<LineaPedido> getPedidos() {
    return Pedidos;
}
public void limpiarPedidos() {
    Pedidos.clear();  
}
//filtros para explorar los pedidos
  public  List<LineaPedido> filtrarPedidosPorCategoria(String opcionSeleccionada) {
      List<LineaPedido> pedidosFiltrados=new ArrayList<>();
    switch(opcionSeleccionada.toLowerCase()){
      case "categoria":
        System.out.println("Filtrando por categoría:");
        //un set para almacenar categorías únicas
        Set<String> categoriasUnicas = new HashSet<>();
        for(LineaPedido pedido:getPedidos()){
          categoriasUnicas.add(pedido.getCategoria());
        }
        //mostrar categorías únicas
        for(String categoria:categoriasUnicas){
          System.out.println("- "+categoria);
          }
      System.out.println("elegir la categoría para filtrar:");
      String categoriaSeleccionada = keyScanner.nextLine();
      boolean categoriaEncontrada=false;
      for(String categoria:categoriasUnicas){
        if(categoria.equalsIgnoreCase(categoriaSeleccionada)){
          categoriaEncontrada=true;
          break;
        }
      }
      if(categoriaEncontrada){
        for(LineaPedido pedido:getPedidos()){
          if(pedido.getCategoria().equalsIgnoreCase(categoriaSeleccionada)){
            pedidosFiltrados.add(pedido);
          }
        }
      }else{
        System.out.println("La categoría seleccionada no se encuentra entre las categorías disponibles.");
      }
        break;
        case "zonacomercial":
         System.out.println("Filtrando por zona comercial:");
         //coger id de zona no repetido
        Set<Integer> zonasComercialesUnicas = new HashSet<>();
    for(LineaPedido pedido:getPedidos()){
        zonasComercialesUnicas.add(pedido.getZonaComercial());
    }
    
    // 2. demustrar
    for(int idZona : zonasComercialesUnicas){
        System.out.println("- ID Zona: " + idZona);
    }
    
    System.out.println("Elegir el ID de la zona comercial para filtrar:");
    String zonacomercialSeleccionada = keyScanner.nextLine();
    int zona=-1;
    try{
           zona=Integer.parseInt(zonacomercialSeleccionada);
        if(zona<0){
          throw  new IllegalArgumentException("Debería ser un número positivo");
        }
    }catch(NumberFormatException exception){
      System.out.println("  No es un número entero.");
      break;
    }catch(IllegalArgumentException exception){
      System.out.println("Error"+exception.getMessage());
      break;
    }
    
    if(zonasComercialesUnicas.contains(zona)){
        for(LineaPedido pedido:getPedidos()){
            if(pedido.getZonaComercial() == zona){
                pedidosFiltrados.add(pedido);
            }
        }
        System.out.println("Filtrado completado.");
    } else {
        System.out.println("La zona comercial seleccionada no se encuentra entre las disponibles.");
    }
    break;
        case "estadopedido":
        System.out.println("Filtrando por estado de pedido:");
        //un set para almacenar estados de pedido únicos
        Set<String> estadosPedidoUnicos = new HashSet<>();
        for(LineaPedido pedido:getPedidos()){
          estadosPedidoUnicos.add(pedido.getEstado().toString());
        }
        //mostrar estados de pedido únicos
        for(String estado:estadosPedidoUnicos){
          System.out.println("- "+estado);
        }
        System.out.println("elegir el estado de pedido para filtrar:");
        String estadoSeleccionado = keyScanner.nextLine();
        boolean estadoEncontrado=false;
        for(String estado:estadosPedidoUnicos){
          if(estado.equalsIgnoreCase(estadoSeleccionado)){
            estadoEncontrado=true;
            break;
          }
        }
        if(estadoEncontrado){
          for(LineaPedido pedido:getPedidos()){
            if(pedido.getEstado().toString().equalsIgnoreCase(estadoSeleccionado)){
              pedidosFiltrados.add(pedido);
            }
          }
        }else{
          System.out.println("El estado de pedido seleccionado no se encuentra entre los estados disponibles.");
        }
        break;
        case  "fecha":
        String fechainput="";
        String fechainput2="";
        boolean fechaValida=false;
        while(!fechaValida){
          System.out.println("Filtrando por fecha de pedido:");
          System.out.println("elegir la fecha de pedido para filtrar  inicial (formato dd-mm-aaaa):");
          fechainput= keyScanner.nextLine();
          System.out.println("elegir la fecha de pedido para filtrar  final (formato dd-mm-aaaa):");
          fechainput2=keyScanner.nextLine();
          try{
            //validar formato de fecha utilizando el DateTimeFormatter definido anteriormente, si la fecha no cumple con el formato esperado se lanzará una excepción que se captura para pedir al usuario que ingrese una fecha válida
              DATE_TIME_FORMATTER.parse(fechainput);
              DATE_TIME_FORMATTER.parse(fechainput2);
              fechaValida=true;
            }catch(Exception e){
              System.out.println("La fecha ingresada tiene un formato incorrecto. Por favor, ingresa una fecha válida en formato dd-mm-aaaa.");
            }
        }
         java.time.LocalDate fecha_inicio=java.time.LocalDate.parse(fechainput,DATE_TIME_FORMATTER);
          java.time.LocalDate fecha_fin=java.time.LocalDate.parse(fechainput2,DATE_TIME_FORMATTER);
          if(fecha_inicio.isAfter(fecha_fin)){
            return  pedidosFiltrados;
          }
    for(LineaPedido pedido:getPedidos()){
        java.time.LocalDate fechaPedido=java.time.LocalDate.parse(pedido.getFechaPedido(),DATE_TIME_FORMATTER);
        if(!fechaPedido.isBefore(fecha_inicio)&&!fechaPedido.isAfter(fecha_fin)){
          pedidosFiltrados.add(pedido);
        }
        }
    break;
    case "NA":
      break;
        default:
        System.out.println("Opción de filtro no válida. Por favor, elige una opción válida.");
    }
    return pedidosFiltrados;
}
}
