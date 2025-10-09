public class Book {
    private String bookName;
    private String author;
    private String type;
    private String publishingHouse;
    private int bookID;
    private int numberOfPage;
    private int yearOfPublication;
    private Boolean availableBook;
    private static int nextBookId=0;

    public Book(String bookName,String author){
        this.bookName=bookName;
        this.author=author;
        this.availableBook=true;
        this.bookID=nextBookId++;
    }

    public String getBookName(){
        return this.bookName;
    }

    public String getAuthor(){
        return this.author;
    }

    public int getBookID(){
        return this.bookID;
    }

    public boolean getAvailableBook(){
        return this.availableBook;
    }

    public String getType() {
        return this.type;
    }

    public int getNumberOfPage() {
        return numberOfPage;
    }

    public int getYearOfPublication() {
        return yearOfPublication;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setBookName(String bookName){
        this.bookName=bookName;
    }

    public void setAuthor(String author){
        this.author=author;
    }

    public void setAvailableBook(boolean availableBook){
        this.availableBook=availableBook;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }

    public void setYearOfPublication(int yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }
}
