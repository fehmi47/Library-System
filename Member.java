public class Member {
    private int ID;
    private String name;
    private String surName;
    private String email;
    public static int nextID=0;

    public Member(String name,String surName,String email){
        this.name=name;
        this.surName=surName;
        this.email=email;
        this.ID=nextID++;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setName(String name) {
        this.name = name;
    }
}
