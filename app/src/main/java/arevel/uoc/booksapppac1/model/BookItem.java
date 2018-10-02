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
    private Date publication;
    private String description;
    private String urlBookFace;

    // Constructor de la clase
    public BookItem(int id, String title, String author, Date publication, String description, String urlBookFace) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publication = publication;
        this.description = description;
        this.urlBookFace = urlBookFace;
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

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
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
}
