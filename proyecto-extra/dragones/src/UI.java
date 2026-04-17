import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class UI {
    public static JFrame frame;

    public static void createWindow(String title, int width, int height) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);
    }

    public static void toggleWindowVisibility() {
        frame.setVisible(!frame.isVisible());
    }

    public static void showMenu() {
        // Limpiar la ventana
        frame.getContentPane().removeAll();

        int btn_width = (int) (frame.getWidth() * 0.3);
        int x_center = (frame.getWidth() - btn_width) / 2;

        int i = 0;

        JLabel titleLabel = new JLabel("Menú de Dragones");
        titleLabel.setBounds(x_center, 10, btn_width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel);

        // Uso LinkedHashMap para respetar el orden
        Map<String, Runnable> options = new LinkedHashMap<>();

        options.put("Añadir dragón", () -> showAddDragonForm());
        options.put("Mostrar listado de dragones", () -> promptFilterAndShowDragons());
        options.put("Exportar lista de dragones", () -> showExportDragons());
        options.put("Salir", () -> System.exit(0));

        for (Map.Entry<String, Runnable> entry : options.entrySet()) {
            JButton button = new JButton(entry.getKey());
            button.addActionListener(e -> entry.getValue().run());
            button.setBounds(x_center, 50 + i++ * 60, btn_width, 50);
            frame.add(button);
        }

        // Refrescar la ventana
        frame.revalidate();
        frame.repaint();
    }

    public static void promptFilterAndShowDragons() {
        // Pregunta YES/NO para aplicar filtro
        int option = JOptionPane.showConfirmDialog(frame, "¿Quieres aplicar un filtro de búsqueda?", "Filtrar Dragones", JOptionPane.YES_NO_OPTION);

        // Creamos una copia de la lista para no modificar la original
        List<Dragon> filteredDragons = new java.util.ArrayList<>(Dragon.getDragons());

        // Si el usuario elige YES
        if (option == JOptionPane.YES_OPTION) {
            String[] atributos = {"ID", "Nombre", "Tipo", "Nivel", "Ataque", "Defensa"};
            String atributoSel = (String) JOptionPane.showInputDialog(frame, "Selecciona el atributo por el que quieres filtrar:", "Filtro", JOptionPane.QUESTION_MESSAGE, null, atributos, atributos[0]);

            if (atributoSel != null) {
                String valor = JOptionPane.showInputDialog(frame, "Introduce el valor exacto para " + atributoSel + ":");
                if (valor != null && !valor.isEmpty()) {
                    // Filtramos la lista según lo introducido
                    filteredDragons.removeIf(d -> {
                        try {
                            return switch (atributoSel) {
                                case "ID" -> d.id != Integer.parseInt(valor);
                                case "Nombre" -> !d.name.equalsIgnoreCase(valor);
                                case "Tipo" -> !d.type.name().equalsIgnoreCase(valor);
                                case "Nivel" -> d.level != Integer.parseInt(valor);
                                case "Ataque" -> d.attack != Integer.parseInt(valor);
                                case "Defensa" -> d.defense != Integer.parseInt(valor);
                                default -> false;
                            };
                        } catch (NumberFormatException e) {
                            return true; // Si pone texto donde va un número, lo descarta
                        }
                    });
                }
            }
        }

        // Ordenamos por ID de menor a mayor
        filteredDragons.sort((d1, d2) -> Integer.compare(d1.id, d2.id));
        
        // Pasamos la lista ya filtrada y ordenada a tu ventana
        showDragons(filteredDragons);
    }

    public static void showAddDragonForm() {
        // Limpiar la ventana
        frame.getContentPane().removeAll();

        int btn_width = (int) (frame.getWidth() * 0.3);
        int x_center = (frame.getWidth() - btn_width) / 2;

        int index = 0;

        JLabel titleLabel = new JLabel("Añadir dragón");
        titleLabel.setBounds(x_center, 10, btn_width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel);

        Map<String, Class<?>> fieldsMap = new LinkedHashMap<>();
        fieldsMap.put("ID", Integer.class);
        fieldsMap.put("Nombre", String.class);
        fieldsMap.put("Tipo", DragonType.class);
        fieldsMap.put("Nivel", Integer.class);
        fieldsMap.put("Ataque", Integer.class);
        fieldsMap.put("Defensa", Integer.class);

        Map<String, JComponent> formFields = new HashMap<>();

        for (Map.Entry<String, Class<?>> entry : fieldsMap.entrySet()) {
            JLabel label = new JLabel(entry.getKey() + ":");
            label.setBounds(x_center - btn_width, 50 + index * 60, btn_width, 50);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            frame.add(label);

            if (entry.getValue() == DragonType.class) {
                // Desplegable con todos los tipos de dragón
                JComboBox<DragonType> field = new JComboBox<>(DragonType.values());
                field.setBounds(x_center, 50 + index * 60, btn_width, 50);
                frame.add(field);
                formFields.put(entry.getKey(), field);
            } else {
                // Por defecto campo de texto
                JTextField field = new JTextField();
                field.setBounds(x_center, 50 + index * 60, btn_width, 50);

                field.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        if (entry.getValue() == Integer.class && !Character.isDigit(e.getKeyChar())) {
                            e.consume(); // Ignorar el carácter si no es un dígito
                        }
                    }
                });

                frame.add(field);
                formFields.put(entry.getKey(), field);
            }          

            index++;
        }

        // Botón para volver al menú
        JButton backButton = new JButton("Volver");
        backButton.setBounds(x_center, 50 + index * 60, btn_width / 3, 50);
        backButton.addActionListener(e -> showMenu());
        frame.add(backButton);
        
        // Botón para guardar el dragón
        JButton saveButton = new JButton("Añadir dragón");
        saveButton.setBounds(x_center + btn_width / 3 , 50 + index * 60, btn_width - btn_width / 3, 50);

        saveButton.addActionListener(e -> {
            try {
                // Obtenemos los valores de los campos
                String idStr = ((JTextField) formFields.get("ID")).getText();
                String name = ((JTextField) formFields.get("Nombre")).getText();
                String levelStr = ((JTextField) formFields.get("Nivel")).getText();
                String attackStr = ((JTextField) formFields.get("Ataque")).getText();
                String defenseStr = ((JTextField) formFields.get("Defensa")).getText();
                DragonType type = (DragonType) ((JComboBox<?>) formFields.get("Tipo")).getSelectedItem();

                // Comprobamos que no haya campos vacíos
                if (idStr.isEmpty() || name.isEmpty() || levelStr.isEmpty() || attackStr.isEmpty() || defenseStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Por favor, llena todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id = Integer.parseInt(idStr);
                int level = Integer.parseInt(levelStr);
                int attack = Integer.parseInt(attackStr);
                int defense = Integer.parseInt(defenseStr);

                if (id <= 0) {
                    JOptionPane.showMessageDialog(frame, "El ID debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (level <= 0) {
                    JOptionPane.showMessageDialog(frame, "El nivel debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (attack <= 0) {
                    JOptionPane.showMessageDialog(frame, "El ataque debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (defense <= 0) {
                    JOptionPane.showMessageDialog(frame, "La defensa debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Intentamos añadir el dragón, si hay algún error lo devuelve
                String resultadoError = Dragon.addDragon(id, name, type, level, attack, defense);

                if (resultadoError != null) {
                    JOptionPane.showMessageDialog(frame, resultadoError, "Error al guardar", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Dragón añadido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    showMenu(); 
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ha ocurrido un error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        frame.add(saveButton);

        // Refrescar la ventana
        frame.revalidate();
        frame.repaint();
    }

    public static void showDragons(List<Dragon> dragons) {
        // Limpiar la ventana
        frame.getContentPane().removeAll();

        int btn_width = (int) (frame.getWidth() * 0.3);
        int x_center = (frame.getWidth() - btn_width) / 2;

        int y = 50;

        JLabel titleLabel = new JLabel("Lista de Dragones");
        titleLabel.setBounds(x_center, 10, btn_width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel);

        // Acciones
        JLabel searchIDLabel = new JLabel("Buscar por ID:");
        searchIDLabel.setBounds(x_center - 200, y, 100, 30);
        frame.add(searchIDLabel);

        JTextField searchIDField = new JTextField();
        searchIDField.setBounds(x_center - 100, y, 100, 30);
        searchIDField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume(); // Ignorar el carácter si no es un dígito
                }
            }
        });
        searchIDField.addActionListener(e -> {
            String idStr = searchIDField.getText();
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    Dragon foundDragon = Dragon.findById(id);
                    if (foundDragon != null) {
                        showDragons(List.of(foundDragon)); // Refrescar la lista con el resultado de la búsqueda

                        return;
                    }

                    showDragons(List.of()); // Si no se encuentra ningún dragón, mostrar lista vacía
                    return;
                } catch (NumberFormatException ex) {
                    // El usuario ha introducido un valor no númerico
                }
            }
            showDragons(Dragon.getDragons()); // Si el campo esta vacío, se muestran todos
        });
        frame.add(searchIDField);

        JLabel searchNameLabel = new JLabel("Buscar por Nombre:");
        searchNameLabel.setBounds(x_center + 100, y, 100, 30);
        frame.add(searchNameLabel);

        JTextField searchNameField = new JTextField();
        searchNameField.setBounds(x_center + 200, y, 100, 30);
        searchNameField.addActionListener(e -> {
            String nameStr = searchNameField.getText();
            if (!nameStr.isEmpty()) {
                List<Dragon> foundDragons = Dragon.findByName(nameStr);
                if (!foundDragons.isEmpty()) {
                    showDragons(foundDragons); // Refrescar la lista con el resultado de la búsqueda
                    return;
                }

                showDragons(List.of()); // Si no se encuentra ningún dragón, mostrar lista vacía
                return;
            }

            showDragons(Dragon.getDragons()); // Si el campo esta vacío, se muestran todos
        });
        frame.add(searchNameField);

        y += 50;

        // Bucle para pintar cada dragón
        for (Dragon dragon : dragons) {
            String info = String.format("ID: %d | Nombre: %s | Tipo: %s | NVL: %d | ATQ: %d | DEF: %d", 
                dragon.id, dragon.name, dragon.type, dragon.level, dragon.attack, dragon.defense);
            
            JLabel dragonLabel = new JLabel(info);
            dragonLabel.setBounds(30, y, frame.getWidth() - 250, 30);
            frame.add(dragonLabel);

            JButton editButton = new JButton("Editar");
            editButton.setBounds(frame.getWidth() - 210, y, 80, 30);
            editButton.addActionListener(e -> editDragon(dragon)); 
            frame.add(editButton);

            JButton deleteButton = new JButton("Eliminar");
            deleteButton.setBounds(frame.getWidth() - 120, y, 80, 30);
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(frame, "¿Borrar a " + dragon.name + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Dragon.deleteById(dragon.id);
                    showDragons(Dragon.getDragons()); 
                }
            });
            frame.add(deleteButton);

            y += 40;
        }

        // Botón para volver al menú
        JButton backButton = new JButton("Volver");
        backButton.setBounds((frame.getWidth() - 100) / 2, y, 100, 50);
        backButton.addActionListener(e -> showMenu());
        frame.add(backButton);

        // Refrescar la ventana
        frame.revalidate();
        frame.repaint();
    }

    public static void editDragon(Dragon dragon) {
        // Limpiar la ventana
        frame.getContentPane().removeAll();

        int btn_width = (int) (frame.getWidth() * 0.3);
        int x_center = (frame.getWidth() - btn_width) / 2;

        int index = 0;

        JLabel titleLabel = new JLabel("Editar dragón con ID: " + dragon.id);
        titleLabel.setBounds(x_center, 10, btn_width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel);

        Map<String, Class<?>> fieldsMap = new LinkedHashMap<>();
        fieldsMap.put("Nombre", String.class);
        fieldsMap.put("Tipo", DragonType.class);
        fieldsMap.put("Nivel", Integer.class);
        fieldsMap.put("Ataque", Integer.class);
        fieldsMap.put("Defensa", Integer.class);

        Map<String, JComponent> formFields = new HashMap<>();

        for (Map.Entry<String, Class<?>> entry : fieldsMap.entrySet()) {
            JLabel label = new JLabel(entry.getKey() + ":");
            label.setBounds(x_center - btn_width, 50 + index * 60, btn_width, 50);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            frame.add(label);

            if (entry.getValue() == DragonType.class) {
                // Desplegable con todos los tipos de dragón
                JComboBox<DragonType> field = new JComboBox<>(DragonType.values());
                field.setBounds(x_center, 50 + index * 60, btn_width, 50);

                switch (entry.getKey()) {
                    case "Tipo" -> field.setSelectedItem(dragon.type);
                    default -> field.setSelectedIndex(0);
                }

                frame.add(field);
                formFields.put(entry.getKey(), field);
            } else {
                // Por defecto campo de texto
                JTextField field = new JTextField();
                field.setBounds(x_center, 50 + index * 60, btn_width, 50);

                switch (entry.getKey()) {
                    case "Nombre" -> field.setText(dragon.name);
                    case "Nivel" -> field.setText(String.valueOf(dragon.level));
                    case "Ataque" -> field.setText(String.valueOf(dragon.attack));
                    case "Defensa" -> field.setText(String.valueOf(dragon.defense));
                    default -> field.setText("");
                }

                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (entry.getValue() == Integer.class && !Character.isDigit(e.getKeyChar())) {
                            e.consume(); // Ignorar el carácter si no es un dígito
                        }
                    }
                });

                frame.add(field);
                formFields.put(entry.getKey(), field);
            }          

            index++;
        }

        // Botón para volver al menú
        JButton backButton = new JButton("Volver");
        backButton.setBounds(x_center, 50 + index * 60, btn_width / 3, 50);
        backButton.addActionListener(e -> showDragons(Dragon.getDragons()));
        frame.add(backButton);
        
        // Botón para guardar el dragón
        JButton saveButton = new JButton("Guardar");
        saveButton.setBounds(x_center + btn_width / 3 , 50 + index * 60, btn_width - btn_width / 3, 50);
        saveButton.addActionListener(e -> {
            try {
                // Obtenemos los valores de los campos
                String name = ((JTextField) formFields.get("Nombre")).getText();
                String levelStr = ((JTextField) formFields.get("Nivel")).getText();
                String attackStr = ((JTextField) formFields.get("Ataque")).getText();
                String defenseStr = ((JTextField) formFields.get("Defensa")).getText();
                DragonType type = (DragonType) ((JComboBox<?>) formFields.get("Tipo")).getSelectedItem();

                // Comprobamos que no haya campos vacíos
                if (name.isEmpty() || levelStr.isEmpty() || attackStr.isEmpty() || defenseStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Por favor, llena todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int level = Integer.parseInt(levelStr);
                int attack = Integer.parseInt(attackStr);
                int defense = Integer.parseInt(defenseStr);

                if (level <= 0) {
                    JOptionPane.showMessageDialog(frame, "El nivel debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (attack <= 0) {
                    JOptionPane.showMessageDialog(frame, "El ataque debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (defense <= 0) {
                    JOptionPane.showMessageDialog(frame, "La defensa debe ser un número entero positivo.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Intentamos editar el dragón, si hay algún error lo devuelve
                String resultadoError = Dragon.editDragon(dragon.id, name, type, level, attack, defense);

                if (resultadoError != null) {
                    JOptionPane.showMessageDialog(frame, resultadoError, "Error al guardar", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Dragón editado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    showDragons(Dragon.getDragons());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ha ocurrido un error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(saveButton);


        // Refrescar la ventana
        frame.revalidate();
        frame.repaint();
    }

    public static void showExportDragons() {
        // Limpiar la ventana
        frame.getContentPane().removeAll();

        int btn_width = (int) (frame.getWidth() * 0.3);
        int x_center = (frame.getWidth() - btn_width) / 2;

        JLabel titleLabel = new JLabel("Exportar lista de dragones");
        titleLabel.setBounds(x_center, 10, btn_width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel);

        int y = 50;

        JButton exportCSVButton = new JButton("Exportar a CSV");
        exportCSVButton.setBounds(x_center, y, btn_width, 50);
        exportCSVButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(frame, "Introduce el nombre para el archivo CSV (ej: mis_dragones.csv):");
            
            // Comprobamos que el usuario no haya cancelado y que no esté vacío
            if (fileName != null && !fileName.trim().isEmpty()) {
                // Le añadimos .csv si al usuario se le ha olvidado
                if (!fileName.toLowerCase().endsWith(".csv")) fileName += ".csv";
                
                boolean success = Utils.exportToCSV(Dragon.getDragons(), fileName);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "Lista de dragones exportada a " + fileName, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Error al exportar la lista de dragones.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.add(exportCSVButton);
        y += 60;

        JButton exportJSONButton = new JButton("Exportar a JSON");
        exportJSONButton.setBounds(x_center, y, btn_width, 50);
        exportJSONButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(frame, "Introduce el nombre para el archivo JSON (ej: mis_dragones.json):");
            
            if (fileName != null && !fileName.trim().isEmpty()) {
                // Le añadimos .json si al usuario se le ha olvidado
                if (!fileName.toLowerCase().endsWith(".json")) fileName += ".json";

                boolean success = Utils.exportToJSON(Dragon.getDragons(), fileName);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "Lista de dragones exportada a " + fileName, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Error al exportar la lista de dragones.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.add(exportJSONButton);
        y += 60;

        // Botón para volver al menú
        JButton backButton = new JButton("Volver");
        backButton.setBounds((frame.getWidth() - 100) / 2, y, 100, 50);
        backButton.addActionListener(e -> showMenu());
        frame.add(backButton);

        // Refrescar la ventana
        frame.revalidate();
        frame.repaint();
    }
}
