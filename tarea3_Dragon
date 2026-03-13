package Proyectoinegenria;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

class dragon11{
	int identificador;
	String nameString;
	String tipo;
	int nivel;
	public dragon11(int identificador,String nameString,String tipo,int nivel) {
		this.identificador=identificador;
		this.nameString=nameString;
		this.tipo=tipo;
		this.nivel=nivel;
		
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getNivel() {
		return nivel;
	}
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
	@Override
	public String toString() {
		return "ID de dragon :" + identificador + "Nombre_de_Dragon :" + nameString + ", Tipo:" + tipo + ", nivel :="
				+ nivel ;
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
	List<dragon11>listasdeDragons=new ArrayList<>();
	public Dragon() {
		
	}
	
public void anadir(int id,String name,String tipo,int nivel) {
	if(!listasdeDragons.contains(new dragon11(id, name, tipo, nivel))) {
		listasdeDragons.add(new dragon11(id, name, tipo, nivel));
     }else {
		System.out.println("ya exite este dragon");
	}
	
}
public void read() {
	for(dragon11 l:listasdeDragons) {
		System.out.println(l);
	}
	
}
public void update(int id,String name,String tipo,int nivel) {
	for(dragon11 l:listasdeDragons) {
		int auxid=l.getIdentificador();
		if(auxid==id) {
			l.setNameString(name);
			l.setTipo(tipo);
			l.setNivel(nivel);	
		}
	}
}
public void delete(int id) {
	for(dragon11 l:listasdeDragons) {
		int auxiid=l.identificador;
		if(auxiid==id) {
			listasdeDragons.remove(l);
			return;
		}
	}
}
public void buscar(int id) {
	for(dragon11 l:listasdeDragons) {
		int auxiid=l.identificador;
		if(auxiid==id) {
			System.out.println(l);
		}
	}
	
}
public static void main(String[] args) {
	Dragon listas=new Dragon();
	Scanner keyScanner=new Scanner(System.in);
	int op=-1;
	do {
		System.out.printf("1. Añadir dragón\n"
				+ "2. Mostrar todos los dragones\n"
				+ "3. Buscar dragón por ID\n"
				+ "4. Modificar dragón\n"
				+ "5. Eliminar dragón\n"
				+ "6. Salir");
		System.out.println("elegir tu opcion");
		 op=keyScanner.nextInt();
		keyScanner.nextLine();
		switch (op) {
		case 1: {
			System.out.println("indroduce id");
			int id=keyScanner.nextInt();
			keyScanner.nextLine();
			System.out.println("indroduce name ");
			String nameString=keyScanner.nextLine();
			System.out.println("indroduce tipo ");
			String tipoString=keyScanner.nextLine();
			System.out.println("indroduce nivel");
			int nivel=keyScanner.nextInt();
			keyScanner.nextLine();
			listas.anadir(id,nameString,tipoString,nivel);
			break;
		}
		case 2: {
			listas.read();
			break;
		}
		case 3: {
			System.out.println("indroduce el id va buscar");
			int id=keyScanner.nextInt();
			listas.buscar(id);
			break;
		}
		case 4: {
			System.out.println("indroduce el id que vas modificar");
			int id=keyScanner.nextInt();
			keyScanner.nextLine();
			System.out.println("indroduce name ");
			String nameString=keyScanner.nextLine();
			System.out.println("indroduce tipo ");
			String tipoString=keyScanner.nextLine();
			System.out.println("indroduce nivel");
			int nivel=keyScanner.nextInt();
			keyScanner.nextLine();
			listas.update(id, nameString, tipoString, nivel);
			break;
		}
		case 5: {
			System.out.println("indroduce el id va eleminar");
			int id=keyScanner.nextInt();
			listas.delete(id);
			break;
		}
		
		}
		
	} while (op!=6);
	keyScanner.close();
}

}
