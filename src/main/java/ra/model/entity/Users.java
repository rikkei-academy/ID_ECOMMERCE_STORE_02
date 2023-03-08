package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Users")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "UserId" )
    private int userId;
    @Column(name = "UserName", unique = true, nullable = false)
    private String userName;
    @Column(name = "UserPassword", nullable = false)
    @JsonIgnore
    private String userPassword;
    @Column(name = "CreatedDate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdDate;
    @Column(name = "Email", nullable = false, unique = true)
    private String email;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "UserStatus")
    private  boolean userStatus;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "User_Role",joinColumns = @JoinColumn(name = "UserId"),
            inverseJoinColumns = @JoinColumn(name = "RoleId"))
    private Set<Roles> listRoles = new HashSet<>();
//    @OneToMany(mappedBy = "users")
//    private List<Order> listOrder = new ArrayList<>();

}
