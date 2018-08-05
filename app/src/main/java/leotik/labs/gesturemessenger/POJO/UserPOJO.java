package leotik.labs.gesturemessenger.POJO;

public class UserPOJO {
    //name
    public String n;
    //picture url
    public String u;
    //phone no
    public String p;
    //email
    public String e;

    public UserPOJO(String Email, String Name) {
        n = Name;
        e = Email;
        p = "";
        u = "";
    }

    public UserPOJO(String Email, String Name, String url, String phone) {
        n = Name;
        e = Email;
        p = phone;
        u = url;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
