package Book;

public class Book {
    private int ID;
    private String name;
    private String shelf;
    private String initAmount;
    private String currentAmount;
    private String author;
    private String publisher;
    private String category;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getInitAmount() {
        return initAmount;
    }

    public void setInitAmount(String initAmount) {
        this.initAmount = initAmount;
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Book() {
    }

    public Book(int ID, String name, String shelf, String initAmount, String currentAmount, String author, String publisher, String category) {
        this.ID = ID;
        this.name = name;
        this.shelf = shelf;
        this.initAmount = initAmount;
        this.currentAmount = currentAmount;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
    }
}
