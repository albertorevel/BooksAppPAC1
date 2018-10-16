package arevel.uoc.booksapppac1.model;

import java.util.Date;

/*
 * Elemento libro representado en el modelo de datos de la aplicación
 */
public class BookItem {

    // Atributos de un libro
    private int id;
    private String title;
    private String author;
    private Date publicationDate;
    private String description;
    private String urlBookFace;
    private int drawableId;

    // Constructor de la clase
    // En el paso 6 se ha añadido el atributo drawableId ya que se muestran diferentes portadas.
    // Esto permite emular la descarga de imágenes desde una url.
    BookItem(int id, String title, String author, Date publicationDate, String description, String urlBookFace, int drawableId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.description = description;
        this.urlBookFace = urlBookFace;
        this.drawableId = drawableId;
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

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlBookFace() {
        return urlBookFace;
    }

    public void setUrlBookFace(String urlBookFace) {
        this.urlBookFace = urlBookFace;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
