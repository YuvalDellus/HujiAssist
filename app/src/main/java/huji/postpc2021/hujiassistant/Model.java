package huji.postpc2021.hujiassistant;

public class Model {

    private String imageUrl;
    private String name;
    private int type;
    private String id;

    public Model() {

    }

    public Model(String imageUrl, String name, int type) {
        this.imageUrl = imageUrl;
        this.type = type;
        this.name = name;
        this.id = "";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
