package Archivo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Archivo {

    //region ATRIBUTOS
    private final String extensionPermitida;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el lector de archivos txt
    */
    public Archivo() {
        this.extensionPermitida = ".txt";
    }
    //endregion

    //region METODOS
    /*
       Lee el contenido completo de un archivo
       @param rutaArchivo Ruta del archivo txt
       @return Contenido del archivo
       @throws IOException Si el archivo no existe o no es txt
    */
    public String leerContenido(String rutaArchivo) throws IOException {
        Path ruta = validarArchivo(rutaArchivo);
        return Files.readString(ruta, StandardCharsets.UTF_8);
    }

    /*
       Valida que el archivo exista y sea txt
       @param rutaArchivo Ruta del archivo
       @return Path validado
       @throws IOException Si la ruta no es valida
    */
    public Path validarArchivo(String rutaArchivo) throws IOException {
        Path ruta = Paths.get(rutaArchivo);

        if (!Files.exists(ruta)) {
            throw new IOException("El archivo no existe: " + rutaArchivo);
        }

        if (!rutaArchivo.toLowerCase().endsWith(extensionPermitida)) {
            throw new IOException("Extension invalida. Se esperaba un archivo .txt");
        }

        return ruta;
    }

    /*
       Obtiene la carpeta donde se generaran las salidas
       @param rutaArchivo Ruta del archivo de entrada
       @return Carpeta de salida
    */
    public Path obtenerCarpetaSalida(String rutaArchivo) {
        Path ruta = Paths.get(rutaArchivo);

        if (ruta.getParent() == null) {
            return Paths.get(".");
        }

        return ruta.getParent();
    }

    /*
       Escribe contenido en un archivo
       @param ruta Ruta del archivo
       @param contenido Contenido a escribir
       @throws IOException Si no se puede escribir
    */
    public void escribirContenido(Path ruta, String contenido) throws IOException {
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
    }
    //endregion
}