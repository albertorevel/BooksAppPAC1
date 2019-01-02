package arevel.uoc.booksapppac1.tools;

import android.support.v4.content.FileProvider;

/**
 * Provider definido para permitir el acceso a ficheros desde otras apps. Lo definimos para evitar
 * posibles conflictos con otros posibles FileProviders que puedan estar definidos en las librerías
 * importadas
 */
public class GenericFileProvider extends FileProvider {
}
