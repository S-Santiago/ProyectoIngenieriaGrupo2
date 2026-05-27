# ProyectoIngenieriaGrupo2

Resumen
-------
Repositorio del Proyecto de Ingeniería (curso 2025-2026). Contiene dos versiones principales del analizador y un proyecto extra de ejemplo.

Estructura principal
-------------------
- `main/V1/`: Versión 1 del proyecto. Contiene el código fuente Java del analizador en `codigo/analizador` (proyecto Maven), documentación y datos de prueba.
- `main/V2/`: Versión 2 del proyecto. Evolución de V1 con más controladores, utilidades y scripts de ejecución (`run_mac.sh`, `run_win.bat`). También es un proyecto Maven bajo `codigo/analizador`.
- `proyecto-extra/dragones/`: Proyecto extra pequeño (Java) con ejemplos y utilidades.

Qué encontrarás en `main/V2/codigo/analizador`
------------------------------------------
- `pom.xml`: configuración Maven.
- `src/`: código fuente Java organizado en paquetes (`app`, `cli`, `controller`, `model`, `persistence`, `view`, `resources`).
- `data/`: archivos de datos usados por la aplicación (`reglas.csv`, `reglas.json`, `zonas.csv`, `zonas.json`).
- `run_mac.sh` / `run_win.bat`: scripts para ejecutar la aplicación en macOS y Windows.

Ejecución rápida
---------------
Requisitos: Java JDK y Maven instalados.

Desde la raíz del módulo (por ejemplo `main/V2/codigo/analizador`):

```bash
# Compilar
mvn -q clean package

# Ejecutar (ejemplo con la clase Main si está configurada)
mvn -q exec:java -Dexec.mainClass="app.Main"

# Alternativamente (macOS), usar el script:
./run_mac.sh
```

Notas
-----
- Revisa `main/V2/codigo/analizador/README.md` para información específica de esa versión.
- Los datos de ejemplo están bajo `data/` en los módulos V1/V2.

Contacto
-------
Equipo del proyecto.
