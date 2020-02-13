package domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Access(AccessType.PROPERTY)
@Table(schema = "TEST", name = "USERS")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Set<User> followers;
    private Set<User> following;

    public User () {};

    public User(String name) {
        this.name = name;
        this.followers = new HashSet<User>();
        this.following = new HashSet<User>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "TEST", name="USER_RELATIONS",
            joinColumns = @JoinColumn(name = "FOLLOWER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FOLLOWED_ID"))
    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public void addFollowing(User followed) {
        following.add(followed);
        followed.followers.add(this);
    }

    public void removeFollowing(User followed) {
        following.remove(followed);
    }

    @ManyToMany(mappedBy = "following")
    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public void addFollower(User follower) {
        followers.add(follower);
        follower.following.add(this);
    }

    public void removeFollower(User follower) {
        followers.remove(follower);
    }
}