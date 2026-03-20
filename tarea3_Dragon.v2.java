package Proyectoinegenria;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

enum Tipo {
	FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD
}

class dragon11 {
	private int identificador;
	private String nameString;
	private Tipo tipo;
	private int nivel;
	private int ATQ;
	private int DEF;

	public dragon11(int identificador, String nameString, Tipo tipo, int nivel, int ATQ, int DEF) {
		this.identificador = identificador;
		this.nameString = nameString;
		this.tipo = tipo;
		this.nivel = nivel;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getATQ() {
		return ATQ;
	}

	public void setATQ(int aTQ) {
		ATQ = aTQ;
	}

	public int getDEF() {
		return DEF;
	}

	public void setDEF(int dEF) {
		DEF = dEF;
	}

	@Override
	public String toString() {
		return "ID de dragon : " + identificador + " Nombre_de_Dragon : " + nameString + " Tipo : " + tipo
				+ ", nivel : " + nivel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identificador, nameString, nivel, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		dragon11 other = (dragon11) obj;
		return identificador == other.identificador && Objects.equals(nameString, other.nameString)
				&& nivel == other.nivel && Objects.equals(tipo, other.tipo);
	}

}

public class Dragon {
	List<dragon11> listasdeDragons = new ArrayList<>();
	Scanner keyScanner = new Scanner(System.in);

	public Dragon() {
		listasdeDragons.add(new dragon11(10, "hola", Tipo.FUEGO, 200, 12, 4));
		listasdeDragons.add(new dragon11(2, "adios", Tipo.AGUA, 500, 18, 6));
		listasdeDragons.add(new dragon11(5, "byebye", Tipo.FUEGO, 2000, 25, 30));
	}

	public void anadir(int id, String name, Tipo tipo, int nivel, int atq, int def) {
		if (!listasdeDragons.contains(new dragon11(id, name, tipo, nivel, atq, def))) {
			listasdeDragons.add(new dragon11(id, name, tipo, nivel, atq, def));
		} else {
			System.out.println("ya exite este dragon");
		}

	}

	public void read() {
		boolean aplicarFiltro;
		boolean respondido = true;

		while (respondido) {
			System.out.println("Quiere aplicar un filtro de búsqueda (YES o NO): ");
			String userInput = keyScanner.nextLine();
			aplicarFiltro = userInput.equalsIgnoreCase("YES");
			if (aplicarFiltro == true) {
				System.out.println("Dime por que criterio quieres filtrar（tipo nivel denfensa id）: ");
				String userInput2 = keyScanner.nextLine();
				if (userInput2.equalsIgnoreCase("Tipo")) {

					Tipo tipoString = null;

					while (tipoString == null) {
						System.out.println("Indroduce tipo (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD)");
						try {
							tipoString = Tipo.valueOf(keyScanner.nextLine().toUpperCase());
							listasdeDragons.sort(Comparator.comparingInt(d -> d.getIdentificador()));
							for (dragon11 dragon : listasdeDragons) {
								if (dragon.getTipo().equals(tipoString)) {
									System.out.println(dragon.getNameString());

								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					return;
				}
				if (userInput2.equalsIgnoreCase("Nivel")) {

					System.out.println("Dime el nivel que quieras filtrar");
					int nivelInput = keyScanner.nextInt();
					listasdeDragons.sort(Comparator.comparingInt(d -> d.getIdentificador()));
					for (dragon11 dragon : listasdeDragons) {
						if (dragon.getNivel() == nivelInput) {
							System.out.println(dragon.getNameString());

						}
					}
					return;
				}
				if (userInput2.equalsIgnoreCase("ataque")) {

					System.out.println("Dime el ataque que quieras filtrar");
					int nivelInput = keyScanner.nextInt();
					listasdeDragons.sort(Comparator.comparingInt(d -> d.getIdentificador()));
					for (dragon11 dragon : listasdeDragons) {
						if (dragon.getATQ() == nivelInput) {
							System.out.println(dragon.getNameString());

						}
					}
					return;
				}
				if (userInput2.equalsIgnoreCase("defensa")) {

					System.out.println("Dime la defensa que quieras filtrar");
					int nivelInput = keyScanner.nextInt();
					listasdeDragons.sort(Comparator.comparingInt(d -> d.getDEF()));
					for (dragon11 dragon : listasdeDragons) {
						if (dragon.getDEF() == nivelInput) {
							System.out.println(dragon.getNameString());
						}
					}
					return;
				}
			} else {
				listasdeDragons.sort(Comparator.comparingInt(d -> d.getIdentificador()));
				for (dragon11 drg : listasdeDragons) {
					System.out.println(drg);
				}
				return;
			}

		}

	}

	public void update() {
		boolean respuesta = false;
		for (dragon11 d : listasdeDragons) {
			System.out.println(d);
		}
		while (!respuesta) {
			System.out.println("quieres modifica por id o por nombre");
			String userintputs = keyScanner.nextLine();

			if (userintputs.equalsIgnoreCase("id")) {
				System.out.println("Ingrese el ID que desea modificar");
				int userintput_id = keyScanner.nextInt();
				for (dragon11 drg : listasdeDragons) {
					if (drg.getIdentificador() == userintput_id) {
						System.out.println("Encontrado,Introduce un nuevo nombre.");
						String nameString = keyScanner.next();
						drg.setNameString(nameString);
						Tipo tipoString = null;
						System.out.println("Indroduce nuevo tipo (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD)");
						while (tipoString == null) {
							try {
								tipoString = Tipo.valueOf(keyScanner.nextLine().toUpperCase());
								drg.setTipo(tipoString);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						int level = -1;
						while (level == -1) {
							System.out.println("indroduce el  nuevo nivel(debe ser positivo");
							level = keyScanner.nextInt();
						}
						drg.setNivel(level);
						System.out.println("indroduce el nuevo  valor de ataque");
						int ata = keyScanner.nextInt();
						drg.setATQ(ata);
						System.out.println("indroduce el  nuevo valor de defensa");
						int def = keyScanner.nextInt();
						drg.setATQ(def);
						System.out.println("modificando correctamente");
						return;
					}
				}
				System.out.println("no encuentro");
				return;
			}
			if (userintputs.equalsIgnoreCase("nombre")) {
				System.out.println("Ingrese el nombre que desea modificar");
				String userintputnameString = keyScanner.nextLine();
				for (dragon11 drg : listasdeDragons) {
					if (drg.getNameString().equalsIgnoreCase(userintputnameString)) {
						System.out.println("Encontrado,Introduce un nuevo id.");
						int nuevoid = keyScanner.nextInt();
						drg.setIdentificador(nuevoid);
						Tipo tipoString = null;
						System.out.println("Indroduce nuevo tipo (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD)");
						while (tipoString == null) {
							try {
								tipoString = Tipo.valueOf(keyScanner.nextLine().toUpperCase());
								drg.setTipo(tipoString);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						int level = -1;
						while (level == -1) {
							System.out.println("indroduce el  nuevo nivel(debe ser positivo");
							level = keyScanner.nextInt();
						}
						drg.setNivel(level);
						System.out.println("indroduce el nuevo  valor de ataque");
						int ata = keyScanner.nextInt();
						drg.setATQ(ata);
						System.out.println("indroduce el  nuevo valor de defensa");
						int def = keyScanner.nextInt();
						drg.setATQ(def);
						System.out.println("modificando correctamente");
						return;
					}
				}
				System.out.println("no encuentro");
				return;
			}

		}
	}

	public void delete(int id) {
		for (dragon11 l : listasdeDragons) {
			int auxiid = l.getIdentificador();
			if (auxiid == id) {
				listasdeDragons.remove(l);
				return;
			}
		}
	}

	public void buscar() {
		System.out.println("Queires buscar informacion de dragon desde el nombre o su identificación?");
		String intputusuario = keyScanner.nextLine();
		if (intputusuario.equalsIgnoreCase("nombre")) {
			System.out.println("De acuerdo. introduce el nombre del dragón que quieres buscar.");
			String intuputString = keyScanner.nextLine();
			for (dragon11 d : listasdeDragons) {
				if (d.getNameString().equalsIgnoreCase(intuputString)) {
					System.out.println("encuentro");
					System.out.println(d);
					return;
				}
			}
			System.out.println("no encuentro");
			return;
		}
		if (intputusuario.equalsIgnoreCase("id")) {
			System.out.println("De acuerdo. introduce el id del dragón que quieres buscar.");
			int intput = keyScanner.nextInt();
			for (dragon11 d : listasdeDragons) {
				if (d.getIdentificador() == intput) {
					System.out.println("encuentro");
					System.out.println(d);
					return;
				}
			}
			System.out.println("no encuentro");
			return;
		}

	}

	public void exportarcsv() {
		System.out.println("indroduce el nombre para archivo ");
		String nombredearchivoString = keyScanner.nextLine();
		String rutaString = nombredearchivoString + ".csv";
		try (PrintWriter escribir = new PrintWriter(new FileWriter(rutaString))) {
			System.out.println("ID,Nombre,TIpo,Nivel,ATaque,Defensa");
			for (dragon11 d : listasdeDragons) {
				escribir.println(d.getIdentificador() + " , " + d.getNameString() + " , " + d.getTipo() + " , "
						+ d.getNivel() + " , " + d.getATQ() + " , " + d.getDEF());
			}
			System.out.println("archivo genrado corrcetamente");

		} catch (Exception e) {
			System.out.println("no se puede genral archivo : " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		Dragon listas = new Dragon();
		Scanner keyScanner = new Scanner(System.in);
		int op = -1;
		do {
			System.out.println();
			System.out.println("---------------------listas de opciones-------------");
			System.out.printf("1. Añadir dragón\n" + "2. Mostrar todos los dragones\n" + "3. Buscar dragón por ID\n"
					+ "4. Modificar dragón\n" + "5. Eliminar dragón\n" + "6. general archivo csv\n" + "0. Salir\n");
			System.out.print("\nElegir tu opcion: ");
			op = keyScanner.nextInt();
			keyScanner.nextLine();
			switch (op) {
			case 1: {
				System.out.println("indroduce id");
				int id = keyScanner.nextInt();
				keyScanner.nextLine();
				System.out.println("indroduce name ");
				String nameString = keyScanner.nextLine();

				Tipo tipoString = null;

				while (tipoString == null) {
					System.out.println("indroduce tipo (FUEGO, AGUA, TIERRA, VIENTO, LUZ, OSCURIDAD)");
					try {
						tipoString = Tipo.valueOf(keyScanner.nextLine().toUpperCase());
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				int nivel = -1;

				while (nivel < 0) {
					System.out.println("indroduce nivel(debe ser positivo)");
					nivel = keyScanner.nextInt();
				}

				keyScanner.nextLine();
				System.out.println("indroduce atq");
				int atq = keyScanner.nextInt();
				System.out.println("indroduce def");
				int def = keyScanner.nextInt();
				listas.anadir(id, nameString, tipoString, nivel, atq, def);
				break;
			}
			case 2: {
				listas.read();
				break;
			}
			case 3: {

				listas.buscar();
				break;
			}
			case 4: {
				listas.update();
				break;
			}
			case 5: {
				System.out.println("indroduce el id va eleminar");
				int id = keyScanner.nextInt();
				listas.delete(id);
				break;
			}
			case 6: {
				listas.exportarcsv();
				break;

			}

			}

		} while (op != 0);
		keyScanner.close();
	}

}
