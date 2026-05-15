package controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.LineaPedido;
import persistence.CsvImporter;

public class ImportKpiController {
       private  ExploradorController exploradorController= ExploradorController.getInstance();
       private  CsvImporter csvImporter=new  CsvImporter();
       public void importar(String s){
        List<LineaPedido>list=csvImporter.importCSVLineaPedidos(s);
        exploradorController.setPedidos(list);
       }    
        public Map<String,Double> calcularKpimensual_facutracion(String opcion){
            List<LineaPedido>listfiltrado=exploradorController.filtrarPedidosPorCategoria(opcion);
            Map<String,Double>kplmensual=new TreeMap<>();
            for(LineaPedido p:listfiltrado){
                java.time.LocalDate Fechadepedido=java.time.LocalDate.parse(p.getFechaPedido(),exploradorController.getdate());
                int mes=Fechadepedido.getMonthValue();
                String meskey=String.format("%02d",mes);
                double facturacion=p.getPrecioVentaUnitario()*p.getUnidades();
                if(kplmensual.containsKey(meskey)){
                    double nuevoprecio=kplmensual.get(meskey)+facturacion;
                    kplmensual.put(meskey, nuevoprecio);
                }else{
                    kplmensual.put(meskey, facturacion);
                }
            }
            return kplmensual;
        }
        public Map<String,Double> calcularKpimensual_margen(String opcion){
            List<LineaPedido>lineaPeddiPedidosfiltrado=exploradorController.filtrarPedidosPorCategoria(opcion);
            Map<String,Double>kplmensual=new TreeMap<>();
            for(LineaPedido p:lineaPeddiPedidosfiltrado){
                java.time.LocalDate Fechadepedido=java.time.LocalDate.parse(p.getFechaPedido(),exploradorController.getdate());
                int mes=Fechadepedido.getMonthValue();
                String meskey=String.format("%02d",mes);
                double margen =(p.getPrecioVentaUnitario() - p.getCosteUnitario()) * p.getUnidades();
                if(kplmensual.containsKey(meskey)){
                    double nuevoprecio=kplmensual.get(meskey)+margen;
                    kplmensual.put(meskey, nuevoprecio);
                }else{
                    kplmensual.put(meskey, margen);
                }
            }
            return kplmensual;
        }
}