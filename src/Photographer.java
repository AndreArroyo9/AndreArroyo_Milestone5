public class Photographer {
    private int PhotographerId;
    private String name;
    private Boolean Awarded;

    public Photographer(int photographerId, String name, Boolean awarded) {
        PhotographerId = photographerId;
        this.name = name;
        Awarded = awarded;
    }

    public int getPhotographerId() {
        return PhotographerId;
    }

    public Boolean getAwarded() {
        return Awarded;
    }

    @Override
    public String toString() {
        return name ;
    }
}
