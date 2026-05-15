package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.LineaPedido;


public class CalculadoraFinanciera {
    //un singleton para compartir la misma instancia de exploradorController entre los controladores
     ExploradorController exploradorController= ExploradorController.getInstance();
     private Map<Integer, Double> margenBrutoPedidos=new HashMap<>();
     private Map<Integer, Double> porcentajeMargenBrutoPedidos=new HashMap<>();
     //un metodo para ordena el valor de mapa
     public  Map<String,Double>ordenMap(Map<String,Double>dMap){
        Set<Map.Entry<String,Double>>map_para_ordenar=dMap.entrySet();
     List<Map.Entry<String,Double>>lista_para_ordenar=new  ArrayList<>(map_para_ordenar);
     lista_para_ordenar.sort((a,b)->b.getValue().compareTo(a.getValue()));
     Map<String,Double>resultado=new LinkedHashMap<>();
     for(Map.Entry<String,Double>d:lista_para_ordenar){
        resultado.put(d.getKey(), d.getValue());
     }
     return  resultado;
     }
        //metodo para calcular el margen bruto total de todos los pedidos, sumando el margen bruto de cada pedido individualmente
        public double calcularMargenBrutoTotal(){
            double margenBrutoTotal=0.0;
            //obtener la lista de pedidos desde el exploradorController, que se supone que ya ha sido cargada con los datos de los pedidos
            List<LineaPedido> pedidos=exploradorController.getPedidos();
            margenBrutoPedidos.clear();
            porcentajeMargenBrutoPedidos.clear();
            for(LineaPedido pedido:pedidos ){
                double costeTotal=pedido.getCosteUnitario()*pedido.getUnidades();
                double precioVentaTotal=pedido.getPrecioVentaUnitario()*pedido.getUnidades();
                double margenBrutoPedido=precioVentaTotal-costeTotal;
                 margenBrutoTotal+=margenBrutoPedido;
                //almacenar el margen bruto de cada pedido en un mapa, utilizando el id del pedido como clave, para poder acceder a él posteriormente si es necesario
                margenBrutoPedidos.put(pedido.getIdLinea(),margenBrutoPedido);
                double porcentaje = 0.0;
            if (precioVentaTotal > 0) {
                porcentaje = (margenBrutoPedido / precioVentaTotal) * 100;
            }
                porcentajeMargenBrutoPedidos.put(pedido.getIdLinea(),porcentaje);
            }
            return margenBrutoTotal;
        }
        //Ranking por Margen (Margen Bruto)
        public Map<String,Double>GenerarRankCategorias(){
            Map<String,Double>Ranking=new HashMap<>();
            List<LineaPedido>Pedidos=exploradorController.getPedidos();
            for(LineaPedido p:Pedidos){
                double costeTotal=p.getCosteUnitario()*p.getUnidades();
                double precioVentaTotal=p.getPrecioVentaUnitario()*p.getUnidades();
                double margenBrutoPedido=precioVentaTotal-costeTotal;
                String categorias=p.getCategoria();
                if(Ranking.containsKey(categorias)){
                    double  nuevoprecio=Ranking.get(categorias)+margenBrutoPedido;
                    Ranking.put(categorias, nuevoprecio);
                }else{
                    Ranking.put(categorias, margenBrutoPedido);
                }

            }

            return  ordenMap(Ranking);
        }
        //Ranking por Facturación （no cuenta coste
        public Map<String, Double> GenerarRankCategoriasPorFacturacion() {
        Map<String, Double> Ranking = new HashMap<>();
        List<LineaPedido> Pedidos = exploradorController.getPedidos();
        for (LineaPedido p : Pedidos) {
        double precioVentaTotal=p.getPrecioVentaUnitario()*p.getUnidades();
        String cat = p.getCategoria();
        if(Ranking.containsKey(cat)){
                    double  nuevoprecio=Ranking.get(cat)+precioVentaTotal;
                    Ranking.put(cat, nuevoprecio);
                }else{
                    Ranking.put(cat, precioVentaTotal);
                }
     }
    
     return ordenMap(Ranking);
}
}