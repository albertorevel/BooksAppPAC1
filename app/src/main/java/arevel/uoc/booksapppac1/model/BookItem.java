package arevel.uoc.booksapppac1.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Elemento libro representado en el modelo de datos de la aplicación
 */
public class BookItem extends RealmObject {

    // Atributos de un libro
    @PrimaryKey
    private int id;

    private String title;
    private String author;
    private String publication_date;
    private String description;
    private String url_image;

    // Usado en PAC1 para simular las portadas de los libros
    // private int drawableId;


    /*
    // Constructor de la clase
    // En el paso 6 se ha añadido el atributo drawableId ya que se muestran diferentes portadas.
    // Esto permite emular la descarga de imágenes desde una url.
    BookItem(int id, String title, String author, String publication_date, String description,
            String url_image, int drawableId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publication_date = publication_date;
        this.description = description;
        this.url_image = url_image;
        this.drawableId = drawableId;
    }
    */

    public BookItem() {
    }

    //Getters y setters de los atributos del libro
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication_date() {
        return publication_date;
    }

    public void setPublication_date(String publication_date) {
        this.publication_date = publication_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
}
