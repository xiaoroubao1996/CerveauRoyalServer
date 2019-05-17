package Model;

import java.io.Serializable;

public class Friends implements Serializable {
    private Integer id;
    private Integer user1Id;
    private Integer user2Id;

    public Friends(User user1, User user2) {
        this.user1Id = user1.getId();
        this.user2Id = user2.getId();
    }
    public Friends(Integer id, Integer user1Id, Integer user2Id) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Integer user1Id) {
        this.user1Id = user1Id;
    }

    public Integer getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Integer user2Id) {
        this.user2Id = user2Id;
    }
}
