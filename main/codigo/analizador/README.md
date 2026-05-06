# Analizador de Rentabilidad de Líneas de Producto

> [!NOTE]
> Este documento detalla la responsabilidad exacta de cada archivo `.java` dentro de la arquitectura MVC y el sistema de persistencia exigido para el **Analizador de Rentabilidad de Líneas de Producto**.

---

## 📂 Estructura del Proyecto

El código fuente está organizado directamente en la carpeta `src/`, separando la lógica en paquetes y ubicando los elementos visuales y de datos en la carpeta `resources/`:
```text
codigo/analizador/
├── pom.xml
├── README.md
└── src/
    ├── controller/
    ├── model/
    ├── persistence/
    ├── resources/
    │   ├── data/
    │   │   ├── reglas.json
    │   │   └── zonas.json
    │   └── fxml/
    │       ├── explorador_pedidos.fxml
    │       ├── importacion_kpis.fxml
    │       └── panel_rentabilidad.fxml
    ├── view/
    └── Main.java